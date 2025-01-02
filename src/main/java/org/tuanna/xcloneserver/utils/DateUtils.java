package org.tuanna.xcloneserver.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

    public static ZonedDateTime convertInstantToZonedDateTime(Instant input) {
        return ZonedDateTime.ofInstant(input, ZoneId.systemDefault());
    }

}
