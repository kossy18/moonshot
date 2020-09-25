/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.xml;

import com.github.kossy18.service.importer.EntityInfo;
import com.github.kossy18.service.importer.Property;
import com.github.kossy18.service.importer.converters.CellConverter;
import com.github.kossy18.service.importer.converters.PropertyConverter;
import com.github.kossy18.service.importer.util.AssertUtils;
import com.github.kossy18.service.importer.util.ReflectionUtils;
import com.github.kossy18.service.importer.util.StringUtils;

import java.util.*;

public final class XmlProcessor {

    // Allow characters, digits, spaces, ^, $, _ and -
    private static final String COLUMN_NAME_PATTERN = "^[\\w_\\s\\-^$.*()]+$";

    private Class<?> clazz;

    private Set<String> columnPatternSet;
    private LinkedList<Property> properties;

    private Map<String, CellConverter> cellConverters;
    private Map<String, PropertyConverter> propertyConverters;

    public XmlProcessor() {
        clazz = null;
        properties = new LinkedList<>();
        columnPatternSet = new HashSet<>();
    }

    public void setGlobalConverters(Map<String, CellConverter> cellConverters, Map<String, PropertyConverter> propertyConverters) {
        this.cellConverters = cellConverters;
        this.propertyConverters = propertyConverters;
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

        properties.add(new Property(name, column, order, verifyAndBuildConverter(true, column, converterRef, converterData)));

        if (!StringUtils.isEmpty(converterRef)) {
            columnPatternSet.add(converterRef);
        }
    }

    public void addColumn(String name, String orderStr, String converterRef, String converterData) {
        Property property = properties.peekLast();

        // Property should not be null here because addProperty must be called before addColumn
        AssertUtils.notNull(property);

        int order = property.getColumnSize() + 1;
        if (!StringUtils.isEmpty(orderStr)) {
            order = Integer.parseInt(orderStr);
        }

        // Avoid duplicate column names
        int prevColSize = columnPatternSet.size();
        columnPatternSet.add(name);

        int newColSize = columnPatternSet.size();
        if (newColSize != (prevColSize + 1)) {
            throw new InvalidMappingException("Column attribute name: " + name + " already exists for property: " + property.getName());
        }
        property.setColumnConverterInfo(order, verifyAndBuildConverter(false, name, converterRef, converterData));
    }

    private Map<String, Property.ConverterInfo> verifyAndBuildConverter(boolean isProperty, String columnName, String converterRef, String converterData) {
        boolean hasConverterRef = !StringUtils.isEmpty(converterRef);
        if (hasConverterRef) {
            boolean inCell = cellConverters.containsKey(converterRef);
            boolean inProperty = propertyConverters.containsKey(converterRef);

            if (!inCell && !inProperty) {
                throw new InvalidMappingException("Property attribute converter-ref: " + converterRef + " not found in the global converter list");
            }
            if (isProperty && inCell) {
                throw new InvalidMappingException("Property attribute converter-ref: " + converterRef + " must implement the PropertyConverter interface");
            }
            if (!isProperty && inProperty) {
                throw new InvalidMappingException("Property attribute converter-ref: " + converterRef + " must implement the CellConverter interface");
            }
        }

        Map<String, Property.ConverterInfo> infoMap = new HashMap<>();
        infoMap.put(columnName, hasConverterRef ? new Property.ConverterInfo(converterRef, converterData) : null);
        return infoMap;
    }

    public void addCellConverter(String name, String className) {
        if (StringUtils.isEmpty(name)) {
            name = className;
        }
        try {
            Class<?> converter = ReflectionUtils.toClass(className);
            if (!ReflectionUtils.isInterfaceOf(converter, CellConverter.class.getName())) {
                throw new InvalidMappingException("CellConverter attribute value: " + className + " does not implement the CellConverter interface");
            }
            cellConverters.put(name, (CellConverter) converter.newInstance());
        } catch (ClassNotFoundException e) {
            throw new InvalidMappingException("CellConverter attribute value: " + className + " could not be found", e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new MappingException("CellConverter attribute value: " + className + " could not be instantiated", e);
        }
    }

    public void addPropertyConverter(String name, String className) {
        if (StringUtils.isEmpty(name)) {
            name = className;
        }
        try {
            Class<?> converter = ReflectionUtils.toClass(className);
            if (!ReflectionUtils.isInterfaceOf(converter, PropertyConverter.class.getName())) {
                throw new InvalidMappingException("PropertyConverter attribute value: " + className + " does not implement the PropertyConverter interface");
            }
            propertyConverters.put(name, (PropertyConverter) converter.newInstance());
        } catch (ClassNotFoundException e) {
            throw new InvalidMappingException("PropertyConverter attribute value: " + className + " could not be found", e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new MappingException("PropertyConverter attribute value: " + className + " could not be instantiated", e);
        }
    }

    public Map<String, CellConverter> getCellConverters() {
        return cellConverters;
    }

    public Map<String, PropertyConverter> getPropertyConverters() {
        return propertyConverters;
    }

    public EntityInfo buildEntityInfo() {
        Map<String, Class<?>[]> setterMethodMap = ReflectionUtils.buildMethodMap(clazz, "set");
        for (Property p : properties) {
            if (p.getColumnSize() == 0) {
                Map<String, Property.ConverterInfo> columnMap = new HashMap<>();
                columnMap.put(p.getColumnMapping(), null);
                p.setColumnConverterInfo(0, columnMap);
            }
            Class<?>[] parameterTypes = setterMethodMap.get(ReflectionUtils.formatMethodName("set", p.getName()));
            if (parameterTypes == null) {
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
