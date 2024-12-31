package org.tuanna.xcloneserver.utils;

import java.time.Instant;

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

}
