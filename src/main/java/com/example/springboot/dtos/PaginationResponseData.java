package com.example.springboot.dtos;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PaginationResponseData<T> {

    private int pageNumber;
    private int pageSize;
    private long totalPages;
    private long totalItems;

    private List<T> items;

}
