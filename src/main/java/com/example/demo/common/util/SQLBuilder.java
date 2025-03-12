package com.example.demo.common.util;

import com.example.demo.common.dto.PaginationResponseData;
import jakarta.persistence.Query;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class SQLBuilder {

    public static final long DEFAULT_PAGE_NUMBER = 1;
    public static final long DEFAULT_PAGE_SIZE = 10;

    public static <T> PaginationResponseData<T> getPaginationResponseData(Long pageNumber, Long pageSize) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageNumber == null || pageNumber <= 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        return PaginationResponseData.<T>builder().pageNumber(pageNumber).pageSize(pageSize).build();
    }

    public static String getPaginationString(long pageNumber, long pageSize) {
        return " limit " + pageSize + " offset " + ((pageNumber - 1) * pageSize);
    }

    static public void setParams(Query query, Map<String, Object> params) {
        if (query == null || MapUtils.isEmpty(params)) return;

        for (Map.Entry<String, Object> item : params.entrySet()) {
            query.setParameter(item.getKey(), item.getValue());
        }
    }

    public static long getTotalPages(long totalItems, long pageSize) {
        return ConversionUtils.safeToLong(totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0));
    }

}
