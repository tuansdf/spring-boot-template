package com.example.sbt.shared.util;

import jakarta.persistence.Tuple;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommonUtils {
    public static <T> T getValue(Tuple tuple, String name, Class<T> tClass) {
        try {
            if (tuple.get(name) == null) {
                return null;
            }
            return tuple.get(name, tClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static String coalesce(String... values) {
        if (ArrayUtils.isEmpty(values)) {
            return null;
        }
        for (String value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static Object coalesce(Object... values) {
        if (ArrayUtils.isEmpty(values)) {
            return null;
        }
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static <T> List<T> padRight(List<T> list, int size, T padValue) {
        if (list == null) {
            list = new ArrayList<>();
        }
        int pads = size - list.size();
        if (pads > 0) {
            list.addAll(Collections.nCopies(pads, padValue));
        }
        return list;
    }

    public static <T> List<T> padRight(List<T> list, int size) {
        return padRight(list, size, null);
    }

    public static String[] padRight(String[] array, int size, String padValue) {
        if (array == null) {
            String[] result = new String[size];
            Arrays.fill(result, padValue);
            return result;
        }
        if (size - array.length > 0) {
            return array;
        }
        String[] result = Arrays.copyOf(array, size);
        Arrays.fill(result, array.length, size, padValue);
        return result;
    }

    public static String[] padRight(String[] array, int size) {
        return padRight(array, size, null);
    }

    public static <T> T get(T[] items, int index) {
        if (items == null || index < 0 || index >= items.length) {
            return null;
        }
        return items[index];
    }

    public static <T> T get(List<T> items, int index) {
        if (items == null || index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    public static <T> List<T> subList(List<T> items, int from, int to) {
        if (CollectionUtils.isEmpty(items)) return new ArrayList<>();
        if (from < 0) from = 0;
        if (to < 0) to = 0;
        if (from >= items.size()) from = items.size() - 1;
        if (to > items.size()) to = items.size();
        if (to <= from) to = from + 1;
        return items.subList(from, to);
    }

    public static <T> T inListOrNull(T input, List<T> values) {
        if (input == null || CollectionUtils.isEmpty(values)) {
            return null;
        }
        if (values.contains(input)) {
            return input;
        }
        return null;
    }

    public static String joinWhenNoNull(String... values) {
        if (ArrayUtils.isEmpty(values)) {
            return "";
        }
        for (String value : values) {
            if (value == null) {
                return "";
            }
        }
        return String.join("", values);
    }

    public static <T> T defaultWhenNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static <T extends Number> T defaultWhenNotPositive(T value, T defaultValue) {
        return value == null || value.doubleValue() <= 0 ? defaultValue : value;
    }

    public static boolean isPositive(Integer value) {
        return value != null && value > 0;
    }
}
