/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer;

import com.andrea.service.importer.entity.Product;
import com.andrea.service.importer.entity.Product2;
import com.andrea.service.importer.reader.DefaultDocumentReaderFactory;
import com.andrea.service.importer.reader.DocumentReaderFactory;
import com.andrea.service.importer.reader.ReaderType;
import com.andrea.service.importer.reader.RowSeeker;
import com.andrea.service.importer.xml.XmlReader;
import com.andrea.service.importer.xml.impl.XmlHandlerImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EntityInfoProcessorTest {
    private static final String XML_FILE_PATH = "src/test/resources/test.xml";
    private static final String CSV_FILE_PATH = "src/test/resources/test.csv";
    private static final String CSV_FILE_PATH_2 = "src/test/resources/test2.csv";
    private static final String XLSX_FILE_PATH = "src/test/resources/test.xlsx";

    private DocumentReaderFactory readerFactory;
    private EntityInfoProcessor processor;

    @Before
    public void init() {
        ImporterConfig config = new ImporterConfig();
        config.setXmlReader(new XmlReader(new XmlHandlerImpl()));
        config.build(XML_FILE_PATH);

        readerFactory = new DefaultDocumentReaderFactory();
        processor = new EntityInfoProcessor<>(config);
    }

    public void processAndGenerateEntityFromCsv_1() throws FileNotFoundException {
        RowSeeker seeker = readerFactory.createReader(ReaderType.CSV).read(new FileInputStream(CSV_FILE_PATH));
        List<Product> products = processor.process(seeker, Product.class);
        seeker.close();

        assertEquals(1, products.size());

        Product product = products.get(0);

        assertEquals(1L, product.getId());
        assertEquals("Book", product.getName());
        assertEquals(5, product.getQuantity());
        assertEquals(5.25f, product.getPrice(), 0.0f);
    }

   @Test
    public void processAndGenerateEntityFromCsv_2() throws FileNotFoundException {
        RowSeeker seeker = readerFactory.createReader(ReaderType.CSV).read(new FileInputStream(CSV_FILE_PATH_2));
        List<Product2> products = processor.process(seeker, Product2.class);
        seeker.close();

        assertEquals(2, products.size());

        Product2 product = products.get(0);

        assertEquals(1L, product.getId());
        assertEquals("Book - 1", product.getName());
        assertEquals(2, product.getQuantity());
        assertEquals(7.875f, product.getPrice(), 0.0f);
    }
}