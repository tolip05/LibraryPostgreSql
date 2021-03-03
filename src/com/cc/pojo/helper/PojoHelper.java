package com.cc.pojo.helper;

import com.cc.db.exception.PojoCreationException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

public class PojoHelper {
    public static boolean getExists(Map<String, Object> resultMap) {
        if (!resultMap.containsKey("exists"))
            throw new RuntimeException("Cannot find mapKey '"
                    + "exists"
                    + "' in result set map!!!!");
        Object value = resultMap.get("exists");
        if (null == value)
            return false;
        else
            return Boolean.valueOf(String.valueOf(value));
    }

    public static String getAsString(Map<String, Object> resultMap,
                                     String mapKey) {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return null;
        else
            return String.valueOf(value);
    }

    public static Date getAsDate(Map<String, Object> resultMap,
                                 String mapKey)
            throws ParseException {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return null;
        else
            return (Date) value;
    }

    public static Date getAsDateTime(Map<String, Object> resultMap,
                                     String mapKey)
            throws ParseException {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return null;
        else
            return (Date) value;
    }

    public static Integer getAsInteger(Map<String, Object> resultMap,
                                       String mapKey) {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return null;
        else
            return Integer.valueOf(value.toString());
    }

    public static Long getAsLong(Map<String, Object> resultMap,
                                 String mapKey) {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return null;
        else
            return Long.valueOf(value.toString());
    }

    public static Float getAsFloat(Map<String, Object> resultMap,
                                   String mapKey) {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return null;
        else
            return Float.valueOf(value.toString());
    }

    public static Double getAsDouble(Map<String, Object> resultMap,
                                     String mapKey) {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return null;
        else
            return Double.valueOf(value.toString());
    }

    public static BigDecimal getAsBigDecimal(Map<String, Object> resultMap,
                                             String mapKey) {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return null;
        else
            return BigDecimal.valueOf(Double.valueOf(value.toString()));
    }

    public static boolean getAsBoolean(Map<String, Object> resultMap,
                                       String mapKey) {
        if (!resultMap.containsKey(mapKey))
            throw new RuntimeException("Cannot find mapKey '"
                    + mapKey
                    + "' in result set map!!!!");
        Object value = resultMap.get(mapKey);
        if (null == value)
            return false;
        else {
            try {
                return Boolean.valueOf(value.toString());
            } catch (Exception e) {

            }
            return (value.toString() == "1") ? true : false;
        }
    }

    public static List<Map<String, Object>> convertArrayAgg(String arrayAgg) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        final String keyPrefix = "v";
        arrayAgg = arrayAgg.substring(1,
                arrayAgg.length()
                        - 1);
        List<String> entries = Arrays.asList(arrayAgg.split("\\),\\("));
        for (String entry : entries) {
            List<String> entryData = Arrays.asList(entry.split(","));
            Map<String, Object> entryDataMap = new LinkedHashMap<String, Object>();
            int entryDataIndex = 1;
            for (String data : entryData) {
                entryDataMap.put(keyPrefix
                                + entryDataIndex,
                        normalizeData(data));
                entryDataIndex++;
            }
            result.add(entryDataMap);
        }
        return result;
    }

    private static String normalizeData(String data) {
        if (data.startsWith("\"")
                && data.endsWith("\""))
            return data.substring(1,
                    data.length()
                            - 1);
        return data;
    }

    public static <T> T fillPojo(Class<T> pojoClass,
                                 Map<String, Object> data)
            throws PojoCreationException {
        return PojoCreationHelper.fillPojo(pojoClass,
                data);
    }

    public static <T> List<T> fillListOfPojos(Class<T> pojoClass,
                                              List<Map<String, Object>> data)
            throws PojoCreationException {
        List<T> result = new ArrayList<T>();
        for (Map<String, Object> singleObjectData : data)
            result.add(fillPojo(pojoClass,
                    singleObjectData));
        return result;
    }

}
