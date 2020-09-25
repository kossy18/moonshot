/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.util;

import com.github.kossy18.service.importer.ImporterException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class FileUtils {

    private FileUtils() {
        // No implementation
    }

    private static final int BUFFER_SIZE = 4096;

    // Does not deal with lines terminated by \r
    public static int countLines(InputStream in) throws IOException {
        try (InputStream is = new BufferedInputStream(in)) {
            byte[] bf = new byte[BUFFER_SIZE];

            int numBytes = is.read(bf);
            if (numBytes == -1) {
                return 0;
            }

            int count = 1;
            while (numBytes != -1) {
                for (int i = 0; i < numBytes; i++) {
                    if (bf[i] == '\n') {
                        ++count;
                    }
                }
                numBytes = is.read(bf);
            }
            return count;
        }
    }

    public static InputStream getFileResourceAsStream(String path) {
        try {
            return getFileResource(path).openStream();
        } catch (IOException e) {
            throw new ImporterException("An error occurred while trying to get inputstream to resource: " + path, e);
        }
    }

    public static URL getFileResource(String path) {
        File f = new File(path);
        if (f.exists()) {
            try {
                return f.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new ImporterException("An error occurred while trying to retrieve file: " + path, e);
            }
        }

        URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        if (resource == null) {
            resource = FileUtils.class.getClassLoader().getResource(path);

            if (resource == null) {
                throw new ImporterException("Could not find the file: " + path);
            }
        }
        return resource;
    }

    public static String getFileExtension(String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }
        int dot = path.lastIndexOf('.');
        int slash = path.lastIndexOf('/');

        return dot > slash ? path.substring(dot + 1) : path.substring(slash + 1);
    }
}
