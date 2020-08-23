/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.utils;

import com.andrea.service.importer.converters.PropertyConverter;

public class PriceConverter implements PropertyConverter {

    @Override
    public Object convert(String extras, Object... args) {
        double cost = (double) args[0];
        double discount = (double) args[1];

        return Double.valueOf((discount * cost) + cost).floatValue();
    }
}
