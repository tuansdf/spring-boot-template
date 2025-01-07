package org.tuanna.xcloneserver.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
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
        if (StringUtils.isEmpty(input)) return null;
        try {
            return UUID.fromString(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long toLong(BigDecimal input) {
        if (input == null) return null;
        try {
            return input.longValue();
        } catch (Exception e) {
            return null;
        }
    }

    public static Long toLong(Object input) {
        if (input == null) return null;
        try {
            return Long.valueOf(String.valueOf(input));
        } catch (Exception e) {
            return null;
        }
    }

}
