/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.xml.impl;

import com.github.kossy18.service.importer.converters.CellConverter;
import com.github.kossy18.service.importer.converters.PropertyConverter;
import com.github.kossy18.service.importer.xml.XmlHandler;
import com.github.kossy18.service.importer.xml.XmlProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;

public class XmlHandlerImpl extends DefaultHandler implements XmlHandler {

    private XmlProcessor processor;
    private Callback mappingCallback;

    private Map<String, CellConverter> cellConverters;
    private Map<String, PropertyConverter> propertyConverters;

    @Override
    public void init() {
        processor = new XmlProcessor();
        processor.setGlobalConverters(cellConverters, propertyConverters);
    }

    public void setGlobalConverters(Map<String, CellConverter> cellConverters, Map<String, PropertyConverter> propertyConverters) {
        this.cellConverters = cellConverters;
        this.propertyConverters = propertyConverters;
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
            case "cell-converter": {
                processor.addCellConverter(attributes.getValue("name"), attributes.getValue("value"));
                break;
            }
            case "property-converter": {
                processor.addPropertyConverter(attributes.getValue("name"), attributes.getValue("value"));
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
                        attributes.getValue("order"),
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
            mappingCallback.mappingResult(processor.buildEntityInfo(), processor.getCellConverters(), processor.getPropertyConverters());
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
