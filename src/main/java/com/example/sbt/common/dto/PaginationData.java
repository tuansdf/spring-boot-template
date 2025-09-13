package com.example.sbt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationData<T> {
    private long pageNumber;
    private long pageSize;
    private long totalPages;
    private long totalItems;

    private List<T> items;
}
