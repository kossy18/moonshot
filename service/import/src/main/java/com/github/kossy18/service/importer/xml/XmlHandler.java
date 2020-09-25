/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.xml;

import com.github.kossy18.service.importer.EntityInfo;
import com.github.kossy18.service.importer.converters.CellConverter;
import com.github.kossy18.service.importer.converters.PropertyConverter;
import org.xml.sax.ContentHandler;

import java.util.Map;

public interface XmlHandler extends ContentHandler {

    void init();

    void addXmlResource(Callback callback);

    void complete();

    interface Callback {
        void readResource(String resource);
        void mappingResult(EntityInfo info, Map<String, CellConverter> cellConverters, Map<String, PropertyConverter> propertyConverters);
    }
}
