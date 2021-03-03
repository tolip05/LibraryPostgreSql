package com.cc.pojo.helper;

import com.cc.annotation.PojoCreator;
import com.cc.annotation.PojoProperty;
import com.cc.db.exception.PojoCreationException;
import com.cc.logging.LOG;
import com.cc.model.IModelWithId;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

public class PojoCreationHelper {

    @SuppressWarnings("unchecked")
    static <T> T fillPojo(Class<T> pojoClass,
                          Map<String, Object> data)
            throws PojoCreationException {
        Constructor<?> annotatedConstructor = findAnnotatedConstructor(pojoClass);
        List<Param> parameters = retreivePropertiesNames(annotatedConstructor);
        List<Object> args = new ArrayList<Object>();
        for (Param param : parameters)
            args.add(obtainValue(data,
                    param));
        int paramIndex = 0;

        for (Class<?> parameterType : annotatedConstructor.getParameterTypes()) {
            Object dataObject = args.get(paramIndex);
            if (dataObject == null) {
                // Set default value if parameterType is primitive
                if (parameterType.equals(byte.class)
                        || parameterType.equals(short.class)
                        || parameterType.equals(int.class))
                    args.set(paramIndex,
                            0);
                else if (parameterType.equals(long.class))
                    args.set(paramIndex,
                            0l);
                else if (parameterType.equals(float.class))
                    args.set(paramIndex,
                            0.0f);
                else if (parameterType.equals(double.class))
                    args.set(paramIndex,
                            0.0d);
                else if (parameterType.equals(boolean.class))
                    args.set(paramIndex,
                            false);
                paramIndex++;
                continue;
            }
            Class<?> dataObjectClass = dataObject.getClass();

            // Checks if parameter in constructor is String and value is not
            // then
            // casts value to String
            if (parameterType.equals(String.class)
                    && !dataObjectClass.equals(String.class))
                args.set(paramIndex,
                        String.valueOf(dataObject));
                // Cast BigDecimal value from db to Long
            else if ((parameterType.equals(long.class)
                    || parameterType.equals(Long.class))
                    && dataObjectClass.equals(BigDecimal.class))
                args.set(paramIndex,
                        Long.valueOf(((BigDecimal) dataObject).longValue()));
                // Cast BigDecimal value from db to Integer
            else if ((parameterType.equals(int.class)
                    || parameterType.equals(Integer.class))
                    && dataObjectClass.equals(BigDecimal.class))
                args.set(paramIndex,
                        Integer.valueOf(((BigDecimal) dataObject).intValue()));
                // Cast Integer value from db to Long
            else if ((parameterType.equals(long.class)
                    || parameterType.equals(Long.class))
                    && dataObjectClass.equals(Integer.class))
                args.set(paramIndex,
                        Long.valueOf((Integer) dataObject));
            paramIndex++;
        }

        T pojo = null;
        try {
            pojo = (T) annotatedConstructor.newInstance(args.toArray());
            List<Field> annotatedFields = findAnnotatedFields(pojoClass);
            if (!annotatedFields.isEmpty())
                fillInFields(annotatedFields,
                        data,
                        pojo);
        } catch (Exception e) {
            LOG.getLog(PojoCreationHelper.class)
                    .error("======== Failed constructor parameters: ===========");
            paramIndex = 0;
            for (Class<?> parameterType : annotatedConstructor.getParameterTypes()) {
                LOG.getLog(PojoCreationHelper.class)
                        .error(parameterType.getName());
                paramIndex++;
                if (paramIndex < annotatedConstructor.getParameterTypes().length)
                    LOG.getLog(PojoCreationHelper.class)
                            .info(", ");
            }
            LOG.getLog(PojoCreationHelper.class)
                    .error("======== Data types: ===========");
            paramIndex = 0;
            for (Object arg : args) {
                if (arg == null)
                    LOG.getLog(PojoCreationHelper.class)
                            .error("Undefined(value is null)");
                else
                    LOG.getLog(PojoCreationHelper.class)
                            .error(arg.getClass()
                                    .getName());
                paramIndex++;
                if (paramIndex < args.size())
                    LOG.getLog(PojoCreationHelper.class)
                            .error(", ");
            }
            throw new PojoCreationException(e);
        }
        return pojo;

    }

    private static List<Field> findAnnotatedFields(Class<?> pojoClass) {
        List<Field> annotatedFields = new ArrayList<Field>();
        for (Field field : pojoClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PojoProperty.class))
                annotatedFields.add(field);
        }
        // Recursively add annotated fields from superclass
        Class<?> superClass = pojoClass.getSuperclass();
        if (superClass != null)
            annotatedFields.addAll(findAnnotatedFields(superClass));
        return annotatedFields;
    }

    private static void fillInFields(List<Field> annotatedFields,
                                     Map<String, Object> data,
                                     Object pojo)
            throws PojoCreationException, IllegalArgumentException, IllegalAccessException {
        for (Field field : annotatedFields) {
            PojoProperty annotation = field.getAnnotation(PojoProperty.class);
            Object value = obtainValue(data,
                    new Param(annotation.value(),
                            field.getType()));
            if (value != null) {
                field.setAccessible(true);
                if (value.getClass()
                        .equals(Long.class)) {
                    Long valueL = (Long) value;
                    if (field.getType()
                            .equals(Long.class)
                            || field.getType()
                            .equals(long.class))
                        value = valueL.longValue();
                    if (field.getType()
                            .equals(Integer.class)
                            || field.getType()
                            .equals(int.class))
                        value = valueL.intValue();
                    if (field.getType()
                            .equals(Short.class)
                            || field.getType()
                            .equals(short.class))
                        value = valueL.shortValue();
                }
                field.set(pojo,
                        value);
            }
        }
    }

    private static Object obtainValue(Map<String, Object> data,
                                      Param param)
            throws PojoCreationException {
        Object paramValue = data.get(param.paramName);
        if (paramValue != null) {
            Class<?> paramType = param.getParamType();
            if (paramType.equals(int.class)
                    || paramType.equals(long.class)
                    || paramType.equals(float.class)
                    || paramType.equals(double.class)
                    || paramType.equals(boolean.class)
                    || paramType.equals(String.class)
                    || paramType.equals(BigDecimal.class)
                    || paramType.equals(Boolean.class)
                    || paramType.equals(Integer.class)
                    || paramType.equals(Long.class)
                    || paramType.equals(Float.class)
                    || paramType.equals(Double.class)
                    || paramType.equals(Date.class)
                    || paramType.equals(byte[].class))
                return paramValue;
            else if (IModelWithId.class.isAssignableFrom(paramType))
                return tryToConstructModelWithId(paramValue,
                        paramType);
            else
                throw new RuntimeException("Do not know what to do with parameter "
                        + param.getParamName());
        }
        return getNullValueForParamType(param.getParamType());
    }

    private static Object tryToConstructModelWithId(Object paramValue,
                                                    Class<?> paramType)
            throws PojoCreationException {
        try {
            for (Constructor<?> constructor : paramType.getConstructors())
                if (constructor.getParameterTypes().length == 1
                        && constructor.getParameterTypes()[0].equals(Long.class)) {

                    Long paramValueAsLong = null;
                    // our databases' ids are numeric so BigDeciml is dataType
                    // in resultset but also account for all numbers
                    if (Number.class.isAssignableFrom(paramValue.getClass()))
                        paramValueAsLong = ((Number) paramValue).longValue();
                    return constructor.newInstance(paramValueAsLong);
                }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new PojoCreationException(e);
        }
        return null;
    }

    private static Object getNullValueForParamType(Class<?> paramType) {
        if (paramType.equals(int.class)
                || paramType.equals(long.class)
                || paramType.equals(float.class)
                || paramType.equals(double.class))
            return 0;
        if (paramType.equals(boolean.class))
            return false;
        return null;
    }

    private static <T> Constructor<?> findAnnotatedConstructor(Class<T> pojoClass) throws PojoCreationException {
        for (Constructor<?> constructor : pojoClass.getConstructors())
            if (constructor.isAnnotationPresent(PojoCreator.class))
                return constructor;
        throw new PojoCreationException("No annotated constructor found");
    }

    @SuppressWarnings({ "unchecked",
            "rawtypes" })
    static <C> Constructor<C> getAppropriateConstructor(Class<C> c,
                                                        List<Object> initArgs) {
        if (initArgs == null)
            initArgs = new ArrayList<Object>();
        for (Constructor con : c.getDeclaredConstructors()) {
            Class<?>[] types = con.getParameterTypes();
            if (types.length != initArgs.size())
                continue;
            boolean match = true;
            for (int i = 0; i < types.length; i++) {
                Class<?> need = types[i], got = initArgs.get(i)
                        .getClass();
                if (!need.isAssignableFrom(got)) {
                    if (need.isPrimitive()) {
                        match = (int.class.equals(need)
                                && Integer.class.equals(got))
                                || (long.class.equals(need)
                                && Long.class.equals(got))
                                || (char.class.equals(need)
                                && Character.class.equals(got))
                                || (short.class.equals(need)
                                && Short.class.equals(got))
                                || (boolean.class.equals(need)
                                && Boolean.class.equals(got))
                                || (byte.class.equals(need)
                                && Byte.class.equals(got));
                    } else {
                        match = false;
                    }
                }
                if (!match)
                    break;
            }
            if (match)
                return con;
        }
        throw new IllegalArgumentException("Cannot find an appropriate constructor for class "
                + c
                + " and arguments "
                + Arrays.toString(initArgs.toArray(new Object[0])));
    }

    private static List<Param> retreivePropertiesNames(Constructor<?> annotatedConstructor) throws PojoCreationException {
        List<Param> properties = new ArrayList<Param>();
        for (int i = 0; i < annotatedConstructor.getParameterTypes().length; i++) {
            Class<?> parameterType = annotatedConstructor.getParameterTypes()[i];
            List<Annotation> parameterAnnotations = Arrays.asList(annotatedConstructor.getParameterAnnotations()[i]);
            PojoProperty pojoPropertyAnnotation = null;
            for (Annotation annotation : parameterAnnotations) {
                if (annotation.annotationType()
                        .equals(PojoProperty.class))
                    pojoPropertyAnnotation = (PojoProperty) annotation;
            }
            if (pojoPropertyAnnotation == null)
                throw new PojoCreationException("All parameters must be annotated with PojoProperty annotation");
            properties.add(new Param(pojoPropertyAnnotation.value(),
                    parameterType));
        }

        return properties;
    }

    static class Param {
        private final String paramName;
        private final Class<?> paramType;

        public Param(String paramName,
                     Class<?> paramType) {
            super();
            this.paramName = paramName;
            this.paramType = paramType;
        }

        public String getParamName() {
            return paramName;
        }

        public Class<?> getParamType() {
            return paramType;
        }

    }
}