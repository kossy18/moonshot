/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.xml.impl;

import com.andrea.service.importer.xml.XmlHandler;
import com.andrea.service.importer.xml.XmlProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandlerImpl extends DefaultHandler implements XmlHandler {

    private XmlProcessor processor;

    private Callback mappingCallback;

    public XmlHandlerImpl() {
        init();
    }

    @Override
    public void init() {
        processor = new XmlProcessor();
    }

    @Override
    public void addXmlResource(Callback callback) {
        this.mappingCallback = callback;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (processor == null) {
            init();
        }
        switch (qName) {
            case "converter": {
                processor.addConverter(attributes.getValue("name"), attributes.getValue("value"));
                break;
            }
            case "class": {
                processor.setClass(attributes.getValue("name"));
                break;
            }
            case "property": {
                processor.addProperty(attributes.getValue("name"),
                        attributes.getValue("column"),
                        attributes.getValue("order"),
                        attributes.getValue("converter-ref"),
                        attributes.getValue("converter-data"));
                break;
            }
            case "column": {
                processor.addColumn(attributes.getValue("name"),
                        attributes.getValue("converter-ref"),
                        attributes.getValue("converter-data"));
                break;
            }
            case "include": {
                mappingCallback.readResource(attributes.getValue("file"));
                break;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("class")) {
            mappingCallback.mappingResult(processor.buildEntityInfo(), processor.getConverters());
            complete();
        }
    }

    @Override
    public void complete() {
        if (processor != null) {
            processor = null;
        }
    }
}
