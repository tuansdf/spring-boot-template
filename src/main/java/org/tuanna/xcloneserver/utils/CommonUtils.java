package org.tuanna.xcloneserver.utils;

import java.util.UUID;

public class CommonUtils {

    public static UUID safeToUUID(String input) {
        try {
            return UUID.fromString(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static String safeToString(Object input) {
        if (input == null) return "";
        try {
            return input.toString();
        } catch (Exception e) {
            return "";
        }
    }

}
