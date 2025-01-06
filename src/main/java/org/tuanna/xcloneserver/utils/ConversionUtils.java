package org.tuanna.xcloneserver.utils;

import java.util.UUID;

public class ConversionUtils {

    public static String safeToString(Object input) {
        if (input == null) return "";
        try {
            return input.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static UUID toUUID(String input) {
        try {
            return UUID.fromString(input);
        } catch (Exception e) {
            return null;
        }
    }

}
