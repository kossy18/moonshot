/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer;

import com.andrea.service.importer.converters.Converter;
import com.andrea.service.importer.reader.Cell;
import com.andrea.service.importer.reader.Row;
import com.andrea.service.importer.reader.RowSeeker;
import com.andrea.service.importer.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public class EntityInfoProcessor<T> {

    private static final int DEFAULT_BATCH_SIZE = 200;

    private ImporterConfig config;

    public EntityInfoProcessor(ImporterConfig config) {
        this.config = config;
    }

    public List<T> process(RowSeeker seeker, Class<T> clazz) {
        final List<T> processedList = new LinkedList<>();
        processImpl(seeker, clazz, DEFAULT_BATCH_SIZE, new EntityInfoProcessorListener<T>() {
            @Override
            public void onProcessed(List<T> processedEntities) {
                processedList.addAll(processedEntities);
            }
        });
        return processedList;
    }

    public void process(RowSeeker seeker, Class<T> clazz, int batchSize, EntityInfoProcessorListener<T> processor) {
        if (batchSize == Integer.MAX_VALUE || batchSize < 0) {
            batchSize = DEFAULT_BATCH_SIZE;
        }
        processImpl(seeker, clazz, batchSize, processor);
    }

    private void processImpl(RowSeeker seeker, Class<T> clazz, int batchSize, EntityInfoProcessorListener<T> processor) {
        EntityInfo info = config.getEntityInfos().get(clazz);
        if (info == null) {
            return;
        }
        Map<String, Converter> converters = config.getConverters();

        int processed = 0;
        List<T> processedEntities = new LinkedList<>();
        while (true) {
            Row row = seeker.next();
            if (row != null) {
                if (row.getIndex() == 0) {
                    // Skip the header
                    continue;
                }
                try {
                    T entity = clazz.newInstance();

                    List<Property> properties = info.getProperties();
                    Map<Cell, Property.ConverterInfo> pendingArgs = new LinkedHashMap<>();
                    for (Iterator<Cell> cellIterator = row.getCells().iterator(); cellIterator.hasNext();) {
                        Cell cell = cellIterator.next();

                        for (Iterator<Property> propertyIterator = properties.iterator(); propertyIterator.hasNext();) {
                            Property property = propertyIterator.next();

                            boolean foundColumn = false;
                            Property.ConverterInfo argConverter = null;

                            for (Map.Entry<Pattern, Property.ConverterInfo> entry : property.getConverterInfoMap().entrySet()) {
                                if (entry.getKey().matcher(cell.getColumnName()).matches()) {
                                    argConverter = entry.getValue();
                                    foundColumn = true;
                                    break;
                                }
                            }
                            if (foundColumn) {
                                if (property.getColumnSize() > 1) {
                                    pendingArgs.put(cell, argConverter);
                                    if (property.getColumnSize() == pendingArgs.size()) {
                                        int index = 0;
                                        Object[] methodArgs = new Object[property.getColumnSize()];
                                        for (Map.Entry<Cell, Property.ConverterInfo> arg : pendingArgs.entrySet()) {
                                            Cell argCell = arg.getKey();
                                            Property.ConverterInfo argConvt = arg.getValue();
                                            if (argConvt != null) {
                                                methodArgs[index] = converters.get(argConvt.getData()).convert(argCell, argConvt.getData());
                                            } else {
                                                methodArgs[index] = argCell.getValue();
                                            }
                                            index++;
                                        }
                                        findAndInvokeMethod(clazz, property, entity, methodArgs);
                                        propertyIterator.remove();
                                    }
                                } else {
                                    Object methodArg = cell.getValue();
                                    if (argConverter != null) {
                                        Converter converter = converters.get(argConverter.getRef());
                                        methodArg = converter.convert(cell, argConverter.getData());
                                    }
                                    findAndInvokeMethod(clazz, property, entity, methodArg);
                                    propertyIterator.remove();
                                }
                                cellIterator.remove();
                                break;
                            }
                        }
                    }
                    processedEntities.add(entity);
                    processed++;
                    if (batchSize == processed) {
                        processor.onProcessed(new LinkedList<>(processedEntities));
                        processed = 0;
                        processedEntities.clear();
                    }
                } catch (Exception e) {
                    throw new ImporterException("An error occurred while processing row: " + row.getIndex(), e);
                }
            } else {
                break;
            }
        }
        if (!processedEntities.isEmpty()) {
            processor.onProcessed(processedEntities);
        }
    }

    private void findAndInvokeMethod(Class<?> clazz, Property property, T classEntity, Object... methodArg) {
        try {
            ReflectionUtils.findAndInvokeMethod(clazz, classEntity, "set", property.getName(), property.getParameterTypes(), methodArg);
        } catch (NoSuchMethodException e) {
            throw new ImporterException("Could not find setter method for field: " + property.getName() + " for class: " + clazz.getName());
        } catch (IllegalArgumentException e) {
            throw new ImporterException("Could not invoke setter method for field: " + property.getName()
                    + " with argument: " + Arrays.toString(methodArg) + ". Verify if a converter is being used", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ImporterException("An error occurred while invoking method for field: " + property.getName(), e);
        }
    }

    public interface EntityInfoProcessorListener<T> {
        void onProcessed(List<T> processedEntities);
    }
}
