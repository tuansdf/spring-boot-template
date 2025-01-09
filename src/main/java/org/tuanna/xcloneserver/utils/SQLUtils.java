package org.tuanna.xcloneserver.utils;

import jakarta.persistence.Query;
import org.apache.commons.collections4.MapUtils;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;

import java.util.Map;

public class SQLUtils {

    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    public static <T> PaginationResponseData<T> getPaginationResponseData(Integer pageNumber, Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageNumber == null || pageNumber <= 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        return PaginationResponseData.<T>builder().pageNumber(pageNumber).pageSize(pageSize).build();
    }

    public static String getPaginationString(Integer pageNumber, Integer pageSize) {
        return " limit " + pageSize + " offset " + ((pageNumber - 1) * pageSize);
    }

    static public void setParams(Query query, Map<String, Object> params) {
        if (query == null || MapUtils.isEmpty(params)) return;

        for (Map.Entry<String, Object> item : params.entrySet()) {
            query.setParameter(item.getKey(), item.getValue());
        }
    }

    public static int getTotalPages(long totalItems, int pageSize) {
        return ConversionUtils.safeToInt(totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0));
    }

}
