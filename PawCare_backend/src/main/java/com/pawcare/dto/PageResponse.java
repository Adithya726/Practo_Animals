package com.pawcare.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/** A stable JSON shape for paged results, independent of Spring's Page serialisation. */
public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
