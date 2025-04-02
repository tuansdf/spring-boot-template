package com.example.sbt.common.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonType {

    public final static String ACCESS_TOKEN = "ACCESS_TOKEN";
    public final static String REFRESH_TOKEN = "REFRESH_TOKEN";
    public final static String RESET_PASSWORD = "RESET_PASSWORD";
    public final static String ACTIVATE_ACCOUNT = "ACTIVATE_ACCOUNT";
    public final static String REACTIVATE_ACCOUNT = "REACTIVATE_ACCOUNT";

    private final static Map<String, Integer> STRING_TO_INDEX;
    private final static List<String> STRINGS;
    private final static int STRINGS_SIZE;

    static {
        STRINGS = List.of(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                RESET_PASSWORD,
                ACTIVATE_ACCOUNT,
                REACTIVATE_ACCOUNT);
        STRINGS_SIZE = STRINGS.size();
        Map<String, Integer> tempStringToIndex = new HashMap<>();
        for (int i = 0; i < STRINGS.size(); i++) {
            tempStringToIndex.put(STRINGS.get(i), i);
        }
        STRING_TO_INDEX = Collections.unmodifiableMap(tempStringToIndex);
    }

    public static String fromIndex(Integer input) {
        if (input == null || input > STRINGS_SIZE) return null;
        return STRINGS.get(input);
    }

    public static Integer toIndex(String input) {
        return STRING_TO_INDEX.get(input);
    }

}
