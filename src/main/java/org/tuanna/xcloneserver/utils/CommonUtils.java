package org.tuanna.xcloneserver.utils;

import java.util.UUID;

public class CommonUtils {

    public static UUID safeToUUID(String input) {
        try {
            return UUID.fromString(input);
        } catch (Exception e) {
            return null;
        }
    }

}
