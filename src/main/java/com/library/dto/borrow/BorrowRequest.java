package com.library.dto.borrow;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Using record for concise DTO
public record BorrowRequest(
        @NotNull(message = "Days to keep must be provided")
        @Min(value = 1, message = "Must keep for at least 1 day")
        Integer daysToKeep
) {}