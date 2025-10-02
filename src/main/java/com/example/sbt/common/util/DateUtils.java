package com.example.sbt.common.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class DateUtils {
    public static long toEpochSeconds(Instant instant) {
        if (instant == null) instant = Instant.now();
        return instant.getEpochSecond();
    }

    public static long currentEpochSeconds() {
        return toEpochSeconds(null);
    }

    public static long toEpochMillis(Instant instant) {
        if (instant == null) instant = Instant.now();
        return instant.toEpochMilli();
    }

    public static long currentEpochMillis() {
        return System.currentTimeMillis();
    }

    public static long toEpochMicros(Instant instant) {
        if (instant == null) instant = Instant.now();
        return instant.getEpochSecond() * 1_000_000L + (instant.getNano() / 1000);
    }

    public static long currentEpochMicros() {
        return toEpochMicros(null);
    }

    public static long toEpochNanos(Instant instant) {
        if (instant == null) instant = Instant.now();
        return instant.getEpochSecond() * 1_000_000_000L + instant.getNano();
    }

    public static long currentEpochNanos() {
        return toEpochNanos(null);
    }

    public static OffsetDateTime toOffsetDateTime(Object input, DateTimeFormatter formatter, ZoneOffset offset) {
        if (formatter == null) formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        try {
            return switch (input) {
                case null -> null;
                case OffsetDateTime v -> v;
                case String v -> OffsetDateTime.parse(v, formatter);
                default -> {
                    Instant result = toInstant(input, formatter, offset);
                    if (result == null) yield null;
                    yield result.atOffset(offset);
                }
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static OffsetDateTime toOffsetDateTime(Object input) {
        return toOffsetDateTime(input, null, null);
    }

    public static OffsetDateTime toOffsetDateTime(Object input, DateTimeFormatter formatter) {
        if (input == null) return null;
        try {
            return toOffsetDateTime(input, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date toDate(Object input, DateTimeFormatter formatter, ZoneOffset offset) {
        try {
            return switch (input) {
                case null -> null;
                case Date v -> v;
                default -> {
                    Instant result = toInstant(input, formatter, offset);
                    if (result == null) yield null;
                    yield Date.from(result);
                }
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Date toDate(Object input) {
        return toDate(input, null, null);
    }

    public static LocalDateTime toLocalDateTime(Object input, DateTimeFormatter formatter, ZoneOffset offset) {
        if (formatter == null) formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        if (offset == null) offset = ZoneOffset.UTC;
        try {
            return switch (input) {
                case null -> null;
                case LocalDateTime v -> v;
                case String v -> LocalDateTime.parse(v, formatter);
                default -> {
                    Instant result = toInstant(input, formatter, offset);
                    if (result == null) yield null;
                    yield LocalDateTime.ofInstant(result, offset);
                }
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDateTime toLocalDateTime(Object input) {
        return toLocalDateTime(input, null, null);
    }

    public static Instant toInstant(Object input, DateTimeFormatter formatter, ZoneOffset offset) {
        if (formatter == null) formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        if (offset == null) offset = ZoneOffset.UTC;
        try {
            return switch (input) {
                case Instant v -> v;
                case Date v -> v.toInstant();
                case LocalDate v -> v.atStartOfDay().toInstant(offset);
                case LocalDateTime v -> v.toInstant(offset);
                case ZonedDateTime v -> v.toInstant();
                case OffsetDateTime v -> v.toInstant();
                case Number v -> Instant.ofEpochMilli(v.longValue());
                case String v -> ZonedDateTime.parse(v, formatter).toInstant();
                case null, default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Instant toInstant(Object input, DateTimeFormatter formatter) {
        return toInstant(input, formatter, null);
    }

    public static Instant toInstant(Object input) {
        return toInstant(input, null, null);
    }

    public static String format(OffsetDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null || formatter == null) return "";
        try {
            return formatter.format(dateTime);
        } catch (Exception e) {
            return "";
        }
    }
}
