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

}
