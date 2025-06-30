package com.example.sbt.common.constant;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class PermissionCode {

    public static final String SYSTEM_ADMIN = "ROLE_P_SYSTEM_ADMIN";
    public static final String READ_USER = "ROLE_P_READ_USER";
    public static final String CREATE_USER = "ROLE_P_CREATE_USER";
    public static final String UPDATE_USER = "ROLE_P_UPDATE_USER";
    public static final String DELETE_USER = "ROLE_P_DELETE_USER";

    private static final Map<String, Integer> STRING_TO_INDEX;
    private static final List<String> STRINGS;
    private static final int STRINGS_SIZE;

    static {
        STRINGS = List.of(
                SYSTEM_ADMIN,
                READ_USER,
                CREATE_USER,
                UPDATE_USER,
                DELETE_USER);
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

    public static List<String> fromIndexes(List<Integer> indexes) {
        if (CollectionUtils.isEmpty(indexes)) return new ArrayList<>();
        return indexes.stream().map(PermissionCode::fromIndex).toList();
    }

    public static List<Integer> toIndexes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) return new ArrayList<>();
        return codes.stream().map(PermissionCode::toIndex).toList();
    }

}
