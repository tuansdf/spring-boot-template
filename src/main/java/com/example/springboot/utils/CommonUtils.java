package com.example.springboot.utils;

import jakarta.persistence.Tuple;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class CommonUtils {

    public static <T> T getValue(Tuple tuple, String name, Class<T> tClass) {
        try {
            if (tuple.get(name) == null) return null;
            return tuple.get(name, tClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static String coalesce(String... values) {
        if (ArrayUtils.isEmpty(values)) return "";
        for (String value : values) {
            if (StringUtils.isNotEmpty(value)) return value;
        }
        return "";
    }

    public static Object coalesce(Object... values) {
        if (ArrayUtils.isEmpty(values)) return null;
        for (Object value : values) {
            if (value != null) return value;
        }
        return null;
    }

}
