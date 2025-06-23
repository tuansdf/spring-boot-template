package com.example.sbt.common.util;

import com.example.sbt.common.dto.PaginationData;
import jakarta.persistence.Query;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class SQLHelper {

    public static final long DEFAULT_PAGE_NUMBER = 1;
    public static final long DEFAULT_PAGE_SIZE = 10;

    public static <T> PaginationData<T> initData(Long pageNumber, Long pageSize) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageNumber == null || pageNumber <= 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        return PaginationData.<T>builder().pageNumber(pageNumber).pageSize(pageSize).build();
    }

    static public void setParams(Query query, Map<String, Object> params) {
        if (query == null || MapUtils.isEmpty(params)) return;
        for (Map.Entry<String, Object> item : params.entrySet()) {
            query.setParameter(item.getKey(), item.getValue());
        }
    }

    public static String toLimitOffset(long pageNumber, long pageSize) {
        return " limit " + pageSize + " offset " + ((pageNumber - 1) * pageSize) + " ";
    }

    public static long toPages(long totalItems, long pageSize) {
        return ConversionUtils.safeToLong(totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0));
    }

    public static String escapeLikePattern(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_")
                .replace("'", "''")
                .replace("\"", "\\\"")
                .replace("[", "\\[")
                .replace("]", "\\]");
    }

}
