package org.tuanna.xcloneserver.constants;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class PermissionCode {

    public static final String SUPER_ADMIN = "ROLE_P_SUPER_ADMIN";
    public static final String READ_USER = "ROLE_P_READ_USER";
    public static final String CREATE_USER = "ROLE_P_CREATE_USER";
    public static final String UPDATE_USER = "ROLE_P_UPDATE_USER";
    public static final String DELETE_USER = "ROLE_P_DELETE_USER";

    private static final Map<String, Integer> stringToIndex;
    private static final Map<Integer, String> indexToString;

    static {
        // WARN: Order matters. Be careful when rearranging in production
        List<String> codes = List.of(SUPER_ADMIN, READ_USER, CREATE_USER, UPDATE_USER, DELETE_USER);
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
            if (StringUtils.isEmpty(code)) continue;
            Integer index = toIndex(code);
            if (index != null) result.add(index);
        }
        return result;
    }

}
