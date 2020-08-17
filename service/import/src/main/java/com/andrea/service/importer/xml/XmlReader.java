/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.xml;

import com.andrea.service.importer.EntityInfo;
import com.andrea.service.importer.ImporterConfig;
import com.andrea.service.importer.converters.Converter;
import com.andrea.service.importer.util.FileUtils;
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
        final Map<String, Converter> converters = new HashMap<>();

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
            public void mappingResult(EntityInfo info, Map<String, Converter> converters1) {
                if (entityInfos.contains(info)) {
                    throw new InvalidMappingException("Cyclic mapping detected. Check for duplicate file include elements");
                }
                entityInfos.add(info);
                converters.putAll(converters1);
            }
        });
        SAXParser parser = factory.newSAXParser();
        parser.parse(FileUtils.getFileResourceAsStream(resource), (DefaultHandler) handler);

        Map<Class<?>, EntityInfo> infoMap = new HashMap<>();
        for (EntityInfo info : entityInfos) {
            infoMap.put(info.getClazz(), info);
        }
        mappingResult.onCall(infoMap, converters);
    }

    public void complete() {
        handler.complete();
    }

    public interface Callback {
        void onCall(Map<Class<?>, EntityInfo> infoMap, Map<String, Converter> converterMap);
    }
}
