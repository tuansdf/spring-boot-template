package com.example.sbt.common.util;

import java.util.UUID;

public class ConversionUtils {

    public static String toString(Object input) {
        try {
            if (input == null) return null;
            return input.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String safeToString(Object input) {
        try {
            if (input == null) return "";
            return input.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String safeTrim(Object input) {
        try {
            if (input == null) return "";
            return input.toString().trim();
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
                case Number v -> v.longValue();
                case String v -> Long.valueOf(v);
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

    public static Integer toInteger(Object input) {
        try {
            return switch (input) {
                case Number v -> v.intValue();
                case String v -> Integer.valueOf(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static int safeToInteger(Object input) {
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

    public static Double toDouble(Object input) {
        try {
            return switch (input) {
                case Number v -> v.doubleValue();
                case String v -> Double.valueOf(v);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static double safeToDouble(Object input) {
        try {
            return switch (input) {
                case Number v -> v.doubleValue();
                case String v -> Double.parseDouble(v);
                case null, default -> 0;
            };
        } catch (Exception e) {
            return 0;
        }
    }

    public static Boolean toBoolean(Object input) {
        try {
            return switch (input) {
                case Boolean v -> v;
                case String v -> Boolean.valueOf(v);
                case Number v -> v.intValue() != 0;
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean safeToBoolean(Object input) {
        try {
            return switch (input) {
                case Boolean v -> v;
                case String v -> Boolean.parseBoolean(v);
                case Number v -> v.intValue() != 0;
                case null, default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T safeCast(Object value) {
        try {
            if (value == null) return null;
            return (T) value;
        } catch (Exception e) {
            return null;
        }
    }

}
