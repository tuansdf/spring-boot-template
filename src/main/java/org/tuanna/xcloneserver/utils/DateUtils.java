package org.tuanna.xcloneserver.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static long toEpochMicro(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
        }
        return instant.getEpochSecond() * 1_000_000L + (instant.getNano() / 1000);
    }

    public static long toEpochNano(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
        }
        return instant.getEpochSecond() * 1_000_000_000L + instant.getNano();
    }

    public static OffsetDateTime toOffsetDateTime(Object input, ZoneOffset offset) {
        if (offset == null) {
            offset = ZoneOffset.UTC;
        }
        try {
            return switch (input) {
                case Instant v -> OffsetDateTime.ofInstant(v, offset);
                case Date v -> v.toInstant().atOffset(offset);
                case LocalDate v -> OffsetDateTime.of(v.atStartOfDay(), offset);
                case LocalDateTime v -> OffsetDateTime.of(v, offset);
                case ZonedDateTime v -> v.toOffsetDateTime();
                case OffsetDateTime v -> v;
                case String v -> toOffsetDateTime(v, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static OffsetDateTime toOffsetDateTime(Object input) {
        return toOffsetDateTime(input, ZoneOffset.UTC);
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
        return toInstant(input, ZoneOffset.UTC);
    }

    public static Instant toInstantSecond(Object input, ZoneOffset offset) {
        if (offset == null) {
            offset = ZoneOffset.UTC;
        }
        try {
            return switch (input) {
                case Number v -> Instant.ofEpochSecond(ConversionUtils.safeToLong(v));
                case String v -> Instant.ofEpochSecond(ConversionUtils.safeToLong(v));
                case null, default -> toInstant(input, offset);
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Instant toInstantSecond(Object input) {
        return toInstantSecond(input, ZoneOffset.UTC);
    }

    public static OffsetDateTime toOffsetDateTime(String input, DateTimeFormatter formatter) {
        if (StringUtils.isEmpty(input)) return null;
        try {
            return OffsetDateTime.parse(input, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toString(OffsetDateTime dateTime, DateTimeFormatter formatter) {
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
        public static final DateTimeFormatter DATE_TIME_BE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    }

}
