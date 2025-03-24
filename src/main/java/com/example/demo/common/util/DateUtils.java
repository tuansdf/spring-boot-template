package com.example.demo.common.util;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static long toEpochMillis(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
        }
        return instant.toEpochMilli();
    }

    public static long currentEpochMillis() {
        return System.currentTimeMillis();
    }

    public static long toEpochMicros(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
        }
        return instant.getEpochSecond() * 1_000_000L + (instant.getNano() / 1000);
    }

    public static long currentEpochMicros() {
        return toEpochMicros(null);
    }

    public static long toEpochNanos(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
        }
        return instant.getEpochSecond() * 1_000_000_000L + instant.getNano();
    }

    public static long currentEpochNanos() {
        return toEpochNanos(null);
    }

    public static OffsetDateTime toOffsetDateTime(Object input, ZoneOffset offset) {
        if (offset == null) {
            offset = ZoneOffset.UTC;
        }
        try {
            return switch (input) {
                case null -> null;
                case OffsetDateTime v -> v;
                case String v -> toOffsetDateTime(v, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                default -> {
                    Instant result = toInstant(input, offset);
                    if (result == null) yield null;
                    yield OffsetDateTime.ofInstant(result, offset);
                }
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Date toDate(Object input, ZoneOffset offset) {
        if (offset == null) {
            offset = ZoneOffset.UTC;
        }
        try {
            return switch (input) {
                case null -> null;
                case Date v -> v;
                default -> {
                    Instant result = toInstant(input, offset);
                    if (result == null) yield null;
                    yield Date.from(result);
                }
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Date toDate(Object input) {
        return toDate(input, null);
    }

    public static OffsetDateTime toOffsetDateTime(Object input) {
        return toOffsetDateTime(input, null);
    }

    public static Instant toInstant(Object input, ZoneOffset offset) {
        if (offset == null) {
            offset = ZoneOffset.UTC;
        }
        try {
            return switch (input) {
                case Instant v -> v;
                case Date v -> v.toInstant();
                case LocalDate v -> v.atStartOfDay().toInstant(offset);
                case LocalDateTime v -> v.toInstant(offset);
                case ZonedDateTime v -> v.toInstant();
                case OffsetDateTime v -> v.toInstant();
                case Number v -> Instant.ofEpochMilli(ConversionUtils.safeToLong(v));
                case String v -> Instant.ofEpochMilli(ConversionUtils.safeToLong(v));
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Instant toInstant(Object input) {
        return toInstant(input, null);
    }

    public static OffsetDateTime toOffsetDateTime(String input, DateTimeFormatter formatter) {
        if (StringUtils.isBlank(input)) return null;
        try {
            return OffsetDateTime.parse(input, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static String format(OffsetDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null || formatter == null) return "";
        try {
            return formatter.format(dateTime);
        } catch (Exception e) {
            return "";
        }
    }

    public static class Format {
        public static final String DATE_TIME_FE = "dd/MM/yyyy HH:mm:ss";
    }

    public static class Formatter {
        public static final DateTimeFormatter ID = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        public static final DateTimeFormatter DATE_TIME_BE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    }

}
