package org.tuanna.xcloneserver.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    public static OffsetDateTime toOffsetDateTime(Object input) {
        try {
            return switch (input) {
                case Instant v -> OffsetDateTime.ofInstant(v, ZoneOffset.systemDefault());
                case String v -> toOffsetDateTime(v, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
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
        public static final DateTimeFormatter DATE_TIME_BE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(ZoneOffset.UTC);
    }

}
