package com.example.springboot.constants;

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

    private static final Map<String, Integer> stringToIndex;
    private static final Map<Integer, String> indexToString;

    static {
        // WARN: Order matters. Be careful when rearranging in production
        List<String> codes = List.of(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                RESET_PASSWORD,
                ACTIVATE_ACCOUNT,
                REACTIVATE_ACCOUNT
        );
        Map<String, Integer> tempStringToIndex = new HashMap<>();
        Map<Integer, String> tempIndexToString = new HashMap<>();
        for (int i = 0; i < codes.size(); i++) {
            tempStringToIndex.put(codes.get(i), i + 1);
            tempIndexToString.put(i + 1, codes.get(i));
        }
        stringToIndex = Collections.unmodifiableMap(tempStringToIndex);
        indexToString = Collections.unmodifiableMap(tempIndexToString);
    }

    public static String fromIndex(Integer input) {
        return indexToString.get(input);
    }

    public static Integer toIndex(String input) {
        return stringToIndex.get(input);
    }

}
