/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class EntityInfo {

    private final Class<?> clazz;

    private final List<Property> properties;

    public EntityInfo(Class<?> clazz, List<Property> properties) {
        this.clazz = clazz;
        this.properties = properties;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public List<Property> getProperties() {
        return new LinkedList<>(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityInfo info = (EntityInfo) o;
        return Objects.equals(clazz, info.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }
}
