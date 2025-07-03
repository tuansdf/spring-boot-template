package com.example.sbt.core.helper;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.shared.util.ConversionUtils;
import jakarta.persistence.Query;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SQLHelper {

    public static final long DEFAULT_PAGE_NUMBER = 1;
    public static final long DEFAULT_PAGE_SIZE = 10;
    public static final List<String> ORDER_DIRECTIONS = List.of("asc", "desc");

    public <T> PaginationData<T> initData(Long pageNumber, Long pageSize) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageNumber == null || pageNumber <= 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        return PaginationData.<T>builder().pageNumber(pageNumber).pageSize(pageSize).build();
    }

    public void setParams(Query query, Map<String, Object> params) {
        if (query == null || MapUtils.isEmpty(params)) return;
        for (Map.Entry<String, Object> item : params.entrySet()) {
            query.setParameter(item.getKey(), item.getValue());
        }
    }

    public String toLimitOffset(long pageNumber, long pageSize) {
        return " limit " + pageSize + " offset " + ((pageNumber - 1) * pageSize) + " ";
    }

    public String toOrderBy(String orderPrefix, String orderBy, String orderDirection, String defaultOrderBy, List<String> validOrderBys) {
        StringBuilder builder = new StringBuilder(" order by ");
        if (StringUtils.isNotBlank(orderBy) && CollectionUtils.isNotEmpty(validOrderBys) && validOrderBys.contains(orderBy)) {
            builder.append(" ").append(orderPrefix).append(orderBy).append(" ");
            if (ORDER_DIRECTIONS.contains(orderDirection)) {
                builder.append(" ").append(orderDirection).append(" ");
            } else {
                builder.append(" asc ");
            }
            if (StringUtils.isNotBlank(defaultOrderBy)) {
                builder.append(" , ");
            }
        }
        if (StringUtils.isNotBlank(defaultOrderBy)) {
            builder.append(" ").append(defaultOrderBy).append(" ");
        }
        return builder.toString();
    }

    public long toPages(long totalItems, long pageSize) {
        return ConversionUtils.safeToLong(totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0));
    }

    public String escapeLikePattern(String input) {
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
