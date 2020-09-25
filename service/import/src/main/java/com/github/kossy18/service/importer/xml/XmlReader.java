/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.xml;

import com.github.kossy18.service.importer.EntityInfo;
import com.github.kossy18.service.importer.ImporterConfig;
import com.github.kossy18.service.importer.converters.CellConverter;
import com.github.kossy18.service.importer.converters.PropertyConverter;
import com.github.kossy18.service.importer.util.FileUtils;
import com.github.kossy18.service.importer.xml.impl.XmlHandlerImpl;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlReader {

    private XmlHandler handler;

    public XmlReader(XmlHandler handler) {
        this.handler = handler;
    }

    public void readXml(String resource, Callback mappingResult) throws ParserConfigurationException, SAXException, IOException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final List<EntityInfo> entityInfos = new ArrayList<>();
        final Map<String, CellConverter> cellConverters = new HashMap<>();
        final Map<String, PropertyConverter> propertyConverters = new HashMap<>();

        ((XmlHandlerImpl)handler).setGlobalConverters(cellConverters, propertyConverters);
        handler.addXmlResource(new XmlHandler.Callback() {
            @Override
            public void readResource(String resource) {
                try {
                    ImporterConfig.validateResource(resource);
                    SAXParser saxParser = factory.newSAXParser();
                    saxParser.parse(FileUtils.getFileResourceAsStream(resource), (DefaultHandler) handler);
                } catch (SAXException | IOException | ParserConfigurationException e) {
                    throw new InvalidMappingException("An error occurred while trying to read resource: " + resource, e);
                }
            }

            @Override
            public void mappingResult(EntityInfo info, Map<String, CellConverter> cellCvt, Map<String, PropertyConverter> propCvt) {
                if (entityInfos.contains(info)) {
                    throw new InvalidMappingException("Cyclic mapping detected. Check for duplicate file include elements");
                }
                entityInfos.add(info);
                cellConverters.putAll(cellCvt);
                propertyConverters.putAll(propCvt);
            }
        });
        SAXParser parser = factory.newSAXParser();
        parser.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        parser.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
        parser.parse(FileUtils.getFileResourceAsStream(resource), (DefaultHandler) handler);

        Map<Class<?>, EntityInfo> infoMap = new HashMap<>();
        for (EntityInfo info : entityInfos) {
            infoMap.put(info.getClazz(), info);
        }
        mappingResult.onCall(infoMap, cellConverters, propertyConverters);
    }

    public void complete() {
        handler.complete();
        handler = null;
    }

    public interface Callback {
        void onCall(Map<Class<?>, EntityInfo> infoMap, Map<String, CellConverter> cellConverterMap, Map<String, PropertyConverter> propertyConverterMap);
    }
}
