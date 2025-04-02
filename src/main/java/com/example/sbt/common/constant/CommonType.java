package com.example.sbt.common.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonType {

    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String RESET_PASSWORD = "RESET_PASSWORD";
    public static final String ACTIVATE_ACCOUNT = "ACTIVATE_ACCOUNT";
    public static final String REACTIVATE_ACCOUNT = "REACTIVATE_ACCOUNT";

    private static final Map<String, Integer> STRING_TO_INDEX;
    private static final List<String> STRINGS;
    private static final int STRINGS_SIZE;

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
