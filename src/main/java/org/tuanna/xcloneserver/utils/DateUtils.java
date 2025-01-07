package org.tuanna.xcloneserver.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static long getEpochMicro(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
        }
        return instant.getEpochSecond() * 1_000_000L + (instant.getNano() / 1000);
    }

    public static long getEpochMicro() {
        return getEpochMicro(null);
    }

    public static OffsetDateTime toOffsetDateTime(String input, DateTimeFormatter formatter) {
        if (StringUtils.isEmpty(input)) return null;
        try {
            return OffsetDateTime.parse(input, formatter);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static OffsetDateTime toOffsetDateTime(Instant input) {
        if (input == null) return null;
        try {
            return OffsetDateTime.ofInstant(input, ZoneOffset.systemDefault());
        } catch (Exception ignored) {
            return null;
        }
    }

    public static ZonedDateTime toZonedDateTime(String input, DateTimeFormatter formatter) {
        if (StringUtils.isEmpty(input)) return null;
        try {
            return ZonedDateTime.parse(input, formatter);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static ZonedDateTime toZonedDateTime(Instant input) {
        if (input == null) return null;
        try {
            return ZonedDateTime.ofInstant(input, ZoneId.systemDefault());
        } catch (Exception e) {
            return null;
        }
    }

    public static String toFormat(OffsetDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null || formatter == null) return "";
        try {
            return formatter.format(dateTime);
        } catch (Exception e) {
            return "";
        }
    }

    public static String toFormat(ZonedDateTime dateTime, DateTimeFormatter formatter) {
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
        public static final DateTimeFormatter DATE_TIME_BE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(ZoneOffset.UTC);
    }

}
