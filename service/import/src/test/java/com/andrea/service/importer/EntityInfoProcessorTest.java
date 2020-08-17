/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer;

import com.andrea.service.importer.entity.Product;
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
    private static final String XLSX_FILE_PATH = "src/test/resources/test.xlsx";

    private DocumentReaderFactory readerFactory;
    private EntityInfoProcessor<Product> processor;

    @Before
    public void init() {
        ImporterConfig config = new ImporterConfig();
        config.setXmlReader(new XmlReader(new XmlHandlerImpl()));
        config.build(XML_FILE_PATH);

        readerFactory = new DefaultDocumentReaderFactory();
        processor = new EntityInfoProcessor<>(config);
    }

    @Test
    public void processAndGenerateEntityFromCsv() throws FileNotFoundException {
        RowSeeker seeker = readerFactory.createReader(ReaderType.CSV).read(new FileInputStream(CSV_FILE_PATH));
        List<Product> products = processor.process(seeker, Product.class);
        seeker.close();

        assertEquals(products.size(), 2);

        Product product = products.get(0);

        assertEquals(product.getId(), 1);
        assertEquals(product.getName(), "Book");
        assertEquals(product.getQuantity(), 5);
        assertEquals(product.getPrice(), 5.25f, 0.0f);
    }

/*    @Test
    public void processAndGenerateEntityFromXlsx() {
        RowSeeker seeker = readerFactory.getReader(DocumentReaderFactory.SPREADSHEET_READER).read(XLSX_FILE_PATH, 1);
        List<Product> products = processor.process(seeker, Product.class);
        seeker.close();

        assertEquals(products.size(), 2);

        Product product = products.get(1);

        assertEquals(product.getId(), 2);
        assertEquals(product.getName(), "Pen");
        assertEquals(product.getQuantity(), 3);
        assertEquals(product.getPrice(), 1.52f, 0.0f);
    }*/
}