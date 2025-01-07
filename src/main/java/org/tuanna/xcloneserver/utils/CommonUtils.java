package org.tuanna.xcloneserver.utils;

import jakarta.persistence.Tuple;

public class CommonUtils {

    public static <T> T getValue(Tuple tuple, String name, Class<T> tClass) {
        try {
            return tuple.get(name, tClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isTrue(Boolean input) {
        try {
            return Boolean.TRUE.equals(input);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFalse(Boolean input) {
        try {
            return Boolean.FALSE.equals(input);
        } catch (Exception e) {
            return false;
        }
    }

}
