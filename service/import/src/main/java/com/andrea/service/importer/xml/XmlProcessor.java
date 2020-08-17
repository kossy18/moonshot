/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.xml;

import com.andrea.service.importer.EntityInfo;
import com.andrea.service.importer.Property;
import com.andrea.service.importer.converters.Converter;
import com.andrea.service.importer.util.AssertUtils;
import com.andrea.service.importer.util.ReflectionUtils;
import com.andrea.service.importer.util.StringUtils;

import java.util.*;

public final class XmlProcessor {

    // Allow characters, digits, spaces, _ and -
    private static final String COLUMN_NAME_PATTERN = "^[\\w_\\s\\-]+$";

    private Class clazz;

    private Set<String> columnPatternSet;

    private LinkedList<Property> properties;

    private Map<String, Converter> converterMap;

    public XmlProcessor() {
        clazz = null;
        converterMap = new HashMap<>();
        properties = new LinkedList<>();
        columnPatternSet = new HashSet<>();
    }

    public void setClass(String className) {
        try {
            clazz = ReflectionUtils.toClass(className);
        } catch (ClassNotFoundException e) {
            throw new InvalidMappingException("Class attribute name: " + className + " could not be found", e);
        }
    }

    public void addProperty(String name, String column, String orderStr, String converterRef, String converterData) {
        if (!StringUtils.isEmpty(column) && !column.matches(COLUMN_NAME_PATTERN)) {
            throw new InvalidMappingException("Property attribute column: " + column + " is not valid");
        }

        if (!ReflectionUtils.isFieldExist(clazz, name)) {
            throw new InvalidMappingException("Property attribute name: " + name + " is not a member of class " + clazz.getName());
        }

        int order = 0;
        try {
            order = Integer.parseInt(orderStr);
        } catch (NumberFormatException ignored) {
            // Ignored
        }

        properties.add(new Property(name, order, verifyAndBuildConverter(column, converterRef, converterData)));

        if (!StringUtils.isEmpty(converterRef)) {
            columnPatternSet.add(converterRef);
        }
    }

    public void addColumn(String name, String converterRef, String converterData) {
        Property property = properties.peekLast();

        // Property should not be null here because addProperty must be called before addColumn
        AssertUtils.notNull(property);

        // Avoid duplicate column names
        int prevColSize = columnPatternSet.size();
        columnPatternSet.add(name);

        int newColSize = columnPatternSet.size();
        if (newColSize != (prevColSize + 1)) {
            throw new InvalidMappingException("Column attribute name: " + name + " already exists for property: " + property.getName());
        }
        property.setConverterMap(verifyAndBuildConverter(name, converterRef, converterData));
    }

    private Map<String, Property.ConverterInfo> verifyAndBuildConverter(String columnName, String converterRef, String converterData) {
        boolean hasConverterRef = !StringUtils.isEmpty(converterRef);

        if (hasConverterRef && !converterMap.containsKey(converterRef)) {
            throw new InvalidMappingException("Property attribute converter-ref: " + converterRef + " not found in the global converter list");
        }

        Map<String, Property.ConverterInfo> columnConverterMap = new HashMap<>();
        columnConverterMap.put(columnName, hasConverterRef ? new Property.ConverterInfo(converterRef, converterData) : null);

        return columnConverterMap;
    }

    public void addConverter(String name, String className) {
        if (StringUtils.isEmpty(name)) {
            name = className;
        }
        try {
            Class converter = ReflectionUtils.toClass(className);
            if (!ReflectionUtils.isInterfaceOf(converter, Converter.class.getName())) {
                throw new InvalidMappingException("Converter attribute value: " + className + " does not implement the Converter interface");
            }
            converterMap.put(name, (Converter) converter.newInstance());
        } catch (ClassNotFoundException e) {
            throw new InvalidMappingException("Converter attribute value: " + className + " could not be found", e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new MappingException("Converter attribute value: " + className + " could not be instantiated", e);
        }
    }

    public Map<String, Converter> getConverters() {
        return converterMap;
    }

    public EntityInfo buildEntityInfo() {
        Map<String, Class<?>[]> setterMethodMap = ReflectionUtils.buildMethodMap(clazz, "set");
        for (Property p : properties) {
            if (p.getColumnSize() == 0) {
                Map<String, Property.ConverterInfo> columnMap = new HashMap<>();
                columnMap.put(p.getName(), null);
                p.setConverterMap(columnMap);
            }
            Class<?>[] parameterTypes = setterMethodMap.get(ReflectionUtils.formatMethodName("set", p.getName()));
            if (parameterTypes != null) {
                if (p.getColumnSize() != parameterTypes.length) {
                    throw new InvalidMappingException("Column length for property " + p.getName()
                            + " differs from it's setter's parameter length by " + Math.abs(parameterTypes.length - p.getColumnSize()));
                }
            } else {
                throw new InvalidMappingException("Setter method for property " + p.getName() + " of class " + clazz.getName() + " does not exist");
            }
            p.setParameterTypes(parameterTypes);
        }
        Collections.sort(properties, new Comparator<Property>() {
            @Override
            public int compare(Property o1, Property o2) {
                if (o1.getOrder() != 0 || o2.getOrder() != 0) {
                    return Integer.compare(o1.getOrder(), o2.getOrder());
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        return new EntityInfo(clazz, properties);
    }

    public void clear() {
        clazz = null;
        properties.clear();
        columnPatternSet.clear();
    }
}
