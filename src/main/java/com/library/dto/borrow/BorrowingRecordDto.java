package com.library.dto.borrow;

import com.library.entity.BorrowStatus;
import java.time.LocalDateTime;

// DTO for returning borrowing record info
public record BorrowingRecordDto(
        Long id,
        Long userId,
        String username, // Include username for convenience
        Long bookId,
        String bookTitle, // Include book title for convenience
        LocalDateTime borrowedDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        BorrowStatus status
) {}