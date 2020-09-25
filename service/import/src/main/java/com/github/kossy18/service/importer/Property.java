/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer;

import com.github.kossy18.service.importer.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

public final class Property {

    private final int order;
    private final String name;
    private final String column;

    private Class<?>[] parameterTypes;

    private Map<Pattern, ConverterInfo> propertyConverterInfo;
    private Map<Pattern, ColumnWrapper> columnConverterInfo;

    public Property(String name, String column, int order, Map<String, ConverterInfo> propertyConverterInfo) {
        this.name = name;
        this.order = order;
        this.column = column;
        setPropertyConverterInfo(propertyConverterInfo);
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public String getColumnMapping() {
        return StringUtils.isEmpty(column) ? name : column;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public int getColumnSize() {
        return columnConverterInfo != null ? columnConverterInfo.size() : 0;
    }

    public Map<Pattern, ColumnWrapper> getColumnConverterInfo() {
        return columnConverterInfo != null ? Collections.unmodifiableMap(columnConverterInfo) : Collections.<Pattern, ColumnWrapper>emptyMap();
    }

    public void setPropertyConverterInfo(Map<String, ConverterInfo> propertyConverterInfo) {
        if (this.propertyConverterInfo == null) {
            this.propertyConverterInfo = new LinkedHashMap<>();
        }
        buildPropertyConverterInfo(propertyConverterInfo);
    }

    private void buildPropertyConverterInfo(Map<String, ConverterInfo> converterInfo) {
        for (Map.Entry<String, ConverterInfo> entry : converterInfo.entrySet()) {
            Pattern pattern = null;
            if (!StringUtils.isEmpty(entry.getKey())) {
                pattern = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);
            }
            if (pattern == null && entry.getValue() == null) {
                continue;
            }
            this.propertyConverterInfo.put(pattern, entry.getValue());
        }
    }

    public void setColumnConverterInfo(int order, Map<String, ConverterInfo> columnConverterInfo) {
        if (this.columnConverterInfo == null) {
            this.columnConverterInfo = new LinkedHashMap<>();
        }
        buildColumnConverterInfo(order, columnConverterInfo);
    }

    private void buildColumnConverterInfo(int order, Map<String, ConverterInfo> converterInfo) {
        for (Map.Entry<String, ConverterInfo> entry : converterInfo.entrySet()) {
            Pattern pattern = null;
            if (!StringUtils.isEmpty(entry.getKey())) {
                pattern = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);
            }
            if (pattern == null && entry.getValue() == null) {
                continue;
            }
            this.columnConverterInfo.put(pattern, new ColumnWrapper(order, entry.getValue()));
        }
    }

    public boolean hasPropertyConverter() {
        return !propertyConverterInfo.isEmpty();
    }

    public ConverterInfo getPropertyConverterInfo() {
        if (!propertyConverterInfo.isEmpty()) {
            for (Map.Entry<Pattern, ConverterInfo> infoEntry : propertyConverterInfo.entrySet()) {
                return infoEntry.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(name, property.name) &&
                Arrays.equals(parameterTypes, property.parameterTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, order);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        return result;
    }

    @Override
    public String toString() {
        return "Property{" +
                "order=" + order +
                ", name='" + name + '\'' +
                ", propertyConverterInfo=" + propertyConverterInfo +
                ", columnConverterInfo=" + columnConverterInfo +
                '}';
    }

    public static class ColumnWrapper {
        private final int order;
        private ConverterInfo info;

        ColumnWrapper(int order, ConverterInfo info) {
            this.order = order;
            this.info = info;
        }

        public int getOrder() {
            return order;
        }

        public ConverterInfo getConverterInfo() {
            return info;
        }
    }

    public static class ConverterInfo {
        private String ref;
        private String data;

        public ConverterInfo(String ref, String data) {
            this.ref = ref;
            this.data = data;
        }

        public String getRef() {
            return ref;
        }

        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "Converter{" +
                    "ref='" + ref + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }
}
