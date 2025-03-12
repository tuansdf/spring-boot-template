package com.example.demo.util;

import jakarta.persistence.Tuple;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    public static <T> List<T> rightPad(List<T> list, int size, T padValue) {
        if (list == null) {
            list = new ArrayList<>();
        }
        while (list.size() < size) {
            list.add(padValue);
        }
        return list;
    }

    public static <T> List<T> rightPad(List<T> list, int size) {
        return rightPad(list, size, null);
    }

}
