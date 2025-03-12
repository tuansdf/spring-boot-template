package com.example.demo.common.constant;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class PermissionCode {

    public static final String SYSTEM_ADMIN = "ROLE_P_SYSTEM_ADMIN";
    public static final String READ_USER = "ROLE_P_READ_USER";
    public static final String CREATE_USER = "ROLE_P_CREATE_USER";
    public static final String UPDATE_USER = "ROLE_P_UPDATE_USER";
    public static final String DELETE_USER = "ROLE_P_DELETE_USER";

    private static final Map<String, Integer> STRING_TO_INDEX;
    private static final Map<Integer, String> INDEX_TO_STRING;

    static {
        // WARN: Order matters. Be careful when rearranging in production
        List<String> codes = List.of(
                SYSTEM_ADMIN,
                READ_USER,
                CREATE_USER,
                UPDATE_USER,
                DELETE_USER
        );
        Map<String, Integer> tempStringToIndex = new HashMap<>();
        Map<Integer, String> tempIndexToString = new HashMap<>();
        for (int i = 0; i < codes.size(); i++) {
            tempStringToIndex.put(codes.get(i), i + 1);
            tempIndexToString.put(i + 1, codes.get(i));
        }
        STRING_TO_INDEX = Collections.unmodifiableMap(tempStringToIndex);
        INDEX_TO_STRING = Collections.unmodifiableMap(tempIndexToString);
    }

    public static String fromIndex(Integer input) {
        return INDEX_TO_STRING.get(input);
    }

    public static Integer toIndex(String input) {
        return STRING_TO_INDEX.get(input);
    }

    public static Set<String> fromIndexes(List<Integer> indexes) {
        Set<String> result = new HashSet<>();
        if (CollectionUtils.isEmpty(indexes)) return result;
        for (Integer index : indexes) {
            String code = fromIndex(index);
            if (code != null) result.add(code);
        }
        return result;
    }

    public static Set<String> fromIndexes(Set<Integer> indexes) {
        Set<String> result = new HashSet<>();
        if (CollectionUtils.isEmpty(indexes)) return result;
        for (Integer index : indexes) {
            String code = fromIndex(index);
            if (code != null) result.add(code);
        }
        return result;
    }

    public static Set<Integer> toIndexes(List<String> codes) {
        Set<Integer> result = new HashSet<>();
        if (CollectionUtils.isEmpty(codes)) return result;
        for (String code : codes) {
            Integer index = toIndex(code);
            if (index != null) result.add(index);
        }
        return result;
    }

    public static Set<Integer> toIndexes(Set<String> codes) {
        Set<Integer> result = new HashSet<>();
        if (CollectionUtils.isEmpty(codes)) return result;
        for (String code : codes) {
            Integer index = toIndex(code);
            if (index != null) result.add(index);
        }
        return result;
    }

}
