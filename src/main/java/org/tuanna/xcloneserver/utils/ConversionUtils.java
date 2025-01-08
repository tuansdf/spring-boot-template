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
                case String v -> Long.valueOf(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

}
