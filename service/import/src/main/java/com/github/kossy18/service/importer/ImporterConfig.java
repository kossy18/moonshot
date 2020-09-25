/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer;

import com.github.kossy18.service.importer.converters.CellConverter;
import com.github.kossy18.service.importer.converters.PropertyConverter;
import com.github.kossy18.service.importer.util.AssertUtils;
import com.github.kossy18.service.importer.util.FileUtils;
import com.github.kossy18.service.importer.xml.MappingException;
import com.github.kossy18.service.importer.xml.XmlReader;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImporterConfig {

    private static final String SCHEMA_FILE_NAME = "service-importer-1.0.xsd";

    private static final String DEFAULT_MAPPING_FILE_NAME = "service-importer-mapping.xml";

    private Map<Class<?>, EntityInfo> entityInfoMap;
    private Map<String, CellConverter> cellConverterMap;
    private Map<String, PropertyConverter> propertyConverterMap;

    private XmlReader xmlReader;

    public ImporterConfig() {
        entityInfoMap = new HashMap<>();
        cellConverterMap = new HashMap<>();
        propertyConverterMap = new HashMap<>();
    }

    public ImporterConfig(XmlReader reader) {
        this();
        this.xmlReader = reader;
    }

    public void setXmlReader(XmlReader reader) {
        this.xmlReader = reader;
    }

    public void build() {
        build(DEFAULT_MAPPING_FILE_NAME);
    }

    public void build(String resource) {
        AssertUtils.notEmpty(resource);

        try {
            AssertUtils.notNull(xmlReader);
            validateResource(resource);
            xmlReader.readXml(resource, new XmlReader.Callback() {
                @Override
                public void onCall(Map<Class<?>, EntityInfo> infoMap, Map<String, CellConverter> cellMap, Map<String, PropertyConverter> propertyMap) {
                    entityInfoMap.putAll(infoMap);
                    cellConverterMap.putAll(cellMap);
                    propertyConverterMap.putAll(propertyMap);
                }
            });
            xmlReader.complete();
        } catch (IOException e) {
            throw new MappingException("An error occurred while reading the resource", e);
        } catch (SAXException | ParserConfigurationException e) {
            throw new MappingException("Unable to map resource", e);
        }
    }

    public Map<Class<?>, EntityInfo> getEntityInfos() {
        return Collections.unmodifiableMap(entityInfoMap);
    }

    public Map<String, CellConverter> getCellConverters() {
        return Collections.unmodifiableMap(cellConverterMap);
    }

    public Map<String, PropertyConverter> getPropertyConverters() {
        return Collections.unmodifiableMap(propertyConverterMap);
    }

    public static void validateResource(String resource) throws IOException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        schemaFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
        Schema schema = schemaFactory.newSchema(FileUtils.getFileResource(SCHEMA_FILE_NAME));

        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(FileUtils.getFileResourceAsStream(resource)));
    }
}
