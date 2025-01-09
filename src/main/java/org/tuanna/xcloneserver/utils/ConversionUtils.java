package org.tuanna.xcloneserver.utils;

import java.math.BigDecimal;
import java.util.UUID;

public class ConversionUtils {

    public static String toString(Object input) {
        if (input == null) return "";
        try {
            return input.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static UUID toUUID(Object input) {
        try {
            return switch (input) {
                case UUID v -> v;
                case String v -> UUID.fromString(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Long toLong(Object input) {
        try {
            return switch (input) {
                case Long v -> v;
                case Integer v -> v.longValue();
                case Double v -> v.longValue();
                case BigDecimal v -> v.longValue();
                case String v -> Long.parseLong(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer toInt(Object input) {
        try {
            return switch (input) {
                case Integer v -> v;
                case Long v -> v.intValue();
                case Double v -> v.intValue();
                case BigDecimal v -> v.intValue();
                case String v -> Integer.parseInt(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static long safeToLong(Object input) {
        Long result = toLong(input);
        if (result == null) {
            return 0L;
        }
        return result;
    }

    public static int safeToInt(Object input) {
        Integer result = toInt(input);
        if (result == null) {
            return 0;
        }
        return result;
    }

}
