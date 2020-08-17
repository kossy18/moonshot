package com.andrea.service.importer;

import com.andrea.service.importer.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class Property {

    private final String name;

    private final int order;

    private Class<?>[] parameterTypes;

    private Map<Pattern, ConverterInfo> converterMap;

    public Property(String name, int order, Map<String, ConverterInfo> columnConverterMap) {
        this.name = name;
        this.order = order;
        setConverterMap(columnConverterMap);
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Map<Pattern, ConverterInfo> getConverterInfoMap() {
        return Collections.unmodifiableMap(converterMap);
    }

    public int getColumnSize() {
        return converterMap.size();
    }

    public void setConverterMap(Map<String, ConverterInfo> columnConverterMap) {
        if (converterMap == null) {
            converterMap = new LinkedHashMap<>(columnConverterMap.size());
        }
        for (Map.Entry<String, ConverterInfo> entry : columnConverterMap.entrySet()) {
            if (!StringUtils.isEmpty(entry.getKey())) {
                converterMap.put(Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE), entry.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return "Property{" +
                "order=" + order +
                ", name='" + name + '\'' +
                ", converterMap=" + converterMap +
                '}';
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
