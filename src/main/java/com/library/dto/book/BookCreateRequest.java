package com.library.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookCreateRequest(
        @NotBlank String title,
        @NotBlank String author,
        String genre,
        @NotBlank @Size(min = 10, max = 20) String isbn,
        @NotNull Integer publishedYear,
        String description,
        String coverUrl,
        @NotNull @Min(1) int totalCopies
) {}