/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer;

import com.github.kossy18.service.importer.converters.CellConverter;
import com.github.kossy18.service.importer.converters.PropertyConverter;
import com.github.kossy18.service.importer.reader.Cell;
import com.github.kossy18.service.importer.reader.Row;
import com.github.kossy18.service.importer.reader.RowSeeker;
import com.github.kossy18.service.importer.util.ReflectionUtils;

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
        int processed = 0;
        List<T> processedEntities = new LinkedList<>();

        EntityInfo info = config.getEntityInfos().get(clazz);
        if (info == null) {
            if (processor != null) {
                processor.onProcessed(processedEntities);
            }
            return;
        }
        List<Property> allProperties = info.getProperties();
        Map<String, CellConverter> allCellConverters = config.getCellConverters();
        Map<String, PropertyConverter> allPropertyConverters = config.getPropertyConverters();

        class CellWrapper implements Comparable<CellWrapper> {
            final int order;
            final Cell cell;

            CellWrapper(int order, Cell cell) {
                this.order = order;
                this.cell = cell;
            }

            @Override
            public int compareTo(CellWrapper o) {
                return Integer.compare(order, o.order);
            }
        }

        while (true) {
            Row row = seeker.next();
            if (row == null) {
                if (processor != null) {
                    processor.onProcessed(processedEntities);
                }
                return;
            }

            if (row.getIndex() == 0) {
                // Skip the header
                continue;
            }
            try {
                T entity = clazz.newInstance();
                Map<CellWrapper, Property.ConverterInfo> cellCvtHolder = new TreeMap<>();

                for (Property property : allProperties) {
                    cellCvtHolder.clear();

                    for (Cell cell : row.getCells()) {
                        int colOrder = -1;
                        boolean foundColumn = false;
                        Property.ConverterInfo colConverter = null;

                        for (Map.Entry<Pattern, Property.ColumnWrapper> entry : property.getColumnConverterInfo().entrySet()) {
                            if (entry.getKey().matcher(cell.getColumnName()).matches()) {
                                foundColumn = true;
                                colOrder = entry.getValue().getOrder();
                                colConverter = entry.getValue().getConverterInfo();
                                break;
                            }
                        }

                        if (foundColumn) {
                            if (property.getColumnSize() == 1) {
                                Object methodArg = cell.getValue();
                                if (colConverter != null) {
                                    CellConverter converter = allCellConverters.get(colConverter.getRef());
                                    methodArg = converter.convert(colConverter.getData(), cell);
                                }
                                if (property.hasPropertyConverter()) {
                                    Property.ConverterInfo propInfo = property.getPropertyConverterInfo();
                                    if (propInfo != null) {
                                        PropertyConverter converter = allPropertyConverters.get(propInfo.getRef());
                                        methodArg = converter.convert(propInfo.getData(), methodArg);
                                    }
                                }
                                findAndInvokeMethod(clazz, property, entity, methodArg);
                                break;
                            } else {
                                cellCvtHolder.put(new CellWrapper(colOrder, cell), colConverter);

                                if (property.getColumnSize() == cellCvtHolder.size()) {
                                    int index = 0;
                                    Object[] methodArgs = new Object[property.getColumnSize()];
                                    for (Map.Entry<CellWrapper, Property.ConverterInfo> arg : cellCvtHolder.entrySet()) {
                                        Property.ConverterInfo cvtInfo = cellCvtHolder.get(arg.getKey());
                                        if (cvtInfo != null) {
                                            CellConverter converter = allCellConverters.get(cvtInfo.getRef());
                                            methodArgs[index++] = converter.convert(cvtInfo.getData(), arg.getKey().cell);
                                        } else {
                                            methodArgs[index++] = arg.getKey().cell.getValue();
                                        }
                                    }
                                    if (property.hasPropertyConverter()) {
                                        Property.ConverterInfo propInfo = property.getPropertyConverterInfo();
                                        PropertyConverter converter = allPropertyConverters.get(propInfo.getRef());
                                        Object arg = converter.convert(propInfo.getData(), methodArgs);
                                        findAndInvokeMethod(clazz, property, entity, arg);
                                    } else {
                                        findAndInvokeMethod(clazz, property, entity, methodArgs);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                processed++;
                processedEntities.add(entity);
                if (batchSize == processed) {
                    processor.onProcessed(new LinkedList<>(processedEntities));
                    processed = 0;
                    processedEntities.clear();
                }
            } catch (Exception e) {
                throw new ImporterException("An error occurred while processing row: " + row.getIndex(), e);
            }
        }
    }

    private void findAndInvokeMethod(Class<?> clazz, Property property, T classEntity, Object... methodArg) {
        try {
            ReflectionUtils.findAndInvokeMethod(clazz, classEntity, "set", property.getName(), property.getParameterTypes(), methodArg);
        } catch (NoSuchMethodException e) {
            throw new ImporterException("Could not find setter method for field: " + property.getName() + " for class: " + clazz.getName());
        } catch (IllegalArgumentException e) {
            throw new ImporterException("Could not invoke setter method for field: " + property.getName()
                    + " with argument: " + Arrays.toString(methodArg) + ". Verify if a converter is being used"
                    + " or check the ordering of the arguments or check if the right types are invoked", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ImporterException("An error occurred while invoking method for field: " + property.getName(), e);
        }
    }

    public interface EntityInfoProcessorListener<T> {
        void onProcessed(List<T> processedEntities);
    }
}
