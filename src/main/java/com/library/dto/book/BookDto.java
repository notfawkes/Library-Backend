package com.library.dto.book;

public record BookDto(
        Long id,
        String title,
        String author,
        String genre,
        String isbn,
        Integer publishedYear,
        String description,
        String coverUrl,
        boolean available,
        int totalCopies,
        int availableCopies
) {}