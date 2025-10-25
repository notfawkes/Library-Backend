package com.library.dto.book;

import org.springframework.data.domain.Page;

import java.util.List;

public record BookPageResponse(
        List<BookDto> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
    public BookPageResponse(Page<BookDto> page) {
        this(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}