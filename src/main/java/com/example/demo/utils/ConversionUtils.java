package com.example.demo.utils;

import java.util.UUID;

public class ConversionUtils {

    public static String toString(Object input) {
        if (input == null) return null;
        try {
            return input.toString();
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

    public static String toCode(Object input) {
        return safeToString(input).trim().toUpperCase();
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
                case Number v -> v.longValue();
                case String v -> Long.parseLong(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static long safeToLong(Object input) {
        try {
            return switch (input) {
                case Number v -> v.longValue();
                case String v -> Long.parseLong(v);
                case null, default -> 0L;
            };
        } catch (Exception e) {
            return 0L;
        }
    }

    public static Integer toInt(Object input) {
        try {
            return switch (input) {
                case Number v -> v.intValue();
                case String v -> Integer.parseInt(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static int safeToInt(Object input) {
        try {
            return switch (input) {
                case Number v -> v.intValue();
                case String v -> Integer.parseInt(v);
                case null, default -> 0;
            };
        } catch (Exception e) {
            return 0;
        }
    }

    public static Boolean toBool(Object input) {
        try {
            return switch (input) {
                case Boolean v -> v;
                case String v -> Boolean.parseBoolean(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean safeToBool(Object input) {
        try {
            return switch (input) {
                case Boolean v -> v;
                case String v -> Boolean.parseBoolean(v);
                case null, default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

}
