package com.example.sbt.shared.util;

import com.example.sbt.core.helper.JSONHelper;
import jakarta.persistence.Tuple;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        if (ArrayUtils.isEmpty(values)) return null;
        for (String value : values) {
            if (StringUtils.isNotEmpty(value)) return value;
        }
        return null;
    }

    public static Object coalesce(Object... values) {
        if (ArrayUtils.isEmpty(values)) return null;
        for (Object value : values) {
            if (value != null) return value;
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

    public static <T> String hashObject(T input) {
        try {
            if (input == null) {
                return null;
            }
            byte[] json = JSONHelper.Mapper.HASHING.writeValueAsBytes(input);
            byte[] hash = DigestUtils.sha256(json);
            return Base64.encodeBase64URLSafeString(hash);
        } catch (Exception e) {
            return null;
        }
    }
}
