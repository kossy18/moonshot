package com.andrea.service.importer.xml;

import com.andrea.service.importer.EntityInfo;
import com.andrea.service.importer.converters.CellConverter;
import com.andrea.service.importer.converters.PropertyConverter;
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
