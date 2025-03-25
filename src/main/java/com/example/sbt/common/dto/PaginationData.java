package com.example.sbt.common.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PaginationData<T> {

    private long pageNumber;
    private long pageSize;
    private long totalPages;
    private long totalItems;

    private List<T> items;

}
