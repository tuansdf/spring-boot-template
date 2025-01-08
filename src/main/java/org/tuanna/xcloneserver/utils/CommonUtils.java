package org.tuanna.xcloneserver.utils;

import jakarta.persistence.Tuple;

public class CommonUtils {

    public static <T> T getValue(Tuple tuple, String name, Class<T> tClass) {
        try {
            if (tuple.get(name) == null) return null;
            return tuple.get(name, tClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isTrue(String input) {
        try {
            return "true".equals(input);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFalse(String input) {
        try {
            return "false".equals(input);
        } catch (Exception e) {
            return false;
        }
    }

}
