package org.tuanna.xcloneserver.constants;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class PermissionCode {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";

    private static final Map<String, Integer> stringToIndex;
    private static final Map<Integer, String> indexToString;

    static {
        List<String> codes = List.of(ADMIN, USER);
        Map<String, Integer> tempStringToIndex = new HashMap<>();
        Map<Integer, String> tempIndexToString = new HashMap<>();
        for (int i = 0; i < codes.size(); i++) {
            tempStringToIndex.put(codes.get(i), i);
            tempIndexToString.put(i, codes.get(i));
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

    public static List<String> fromIndexes(List<Integer> indexes) {
        List<String> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(indexes)) return result;
        for (Integer index : indexes) {
            String code = fromIndex(index);
            if (code != null) result.add(code);
        }
        return result;
    }

    public static List<Integer> toIndexes(List<String> codes) {
        List<Integer> result = new ArrayList<>();
        for (String code : codes) {
            Integer index = toIndex(code);
            if (index != null) result.add(index);
        }
        return result;
    }

}
