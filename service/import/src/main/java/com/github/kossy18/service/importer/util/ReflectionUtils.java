/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Only intended for internal use.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        // No implementation
    }

    public static Class<?> toClass(String name) throws ClassNotFoundException {
        AssertUtils.notEmpty(name);
        return Class.forName(name);
    }

    public static boolean isFieldExist(Class<?> clazz, String name) {
        try {
            clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return false;
        }
        return true;
    }

    public static boolean isInterfaceOf(Class<?> clazz, String interfaceName) {
        boolean found = false;
        for (Class<?> anInterface : clazz.getInterfaces()) {
            if (anInterface.getName().equals(interfaceName)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public static Map<String, Class<?>[]> buildMethodMap(Class<?> clazz, String prefix) {
        Map<String, Class<?>[]> methodMap = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getParameterTypes().length > 0 && method.getName().startsWith(prefix)) {
                methodMap.put(method.getName(), method.getParameterTypes());
            }
        }
        return methodMap;
    }

    public static <T> void findAndInvokeMethod(Class<?> clazz, T classEntity, String methodPrefix, String fieldName, Class<?>[] parameterTypes, Object... methodArg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getMethod(formatMethodName(methodPrefix, fieldName), parameterTypes);
        method.invoke(classEntity, methodArg);
    }

    public static String formatMethodName(String prefix, String fieldName) {
        return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
