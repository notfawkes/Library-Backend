package com.library.dto.borrow;

import com.library.dto.book.BookDto; // Assuming you have a BookDto
import com.library.entity.BorrowStatus;
import java.time.LocalDateTime;

// DTO specifically for the /api/user/borrowed-books endpoint
// Includes full book details and borrowing info
public record BorrowedBookDto(
        // Borrowing details
        Long borrowingRecordId,
        LocalDateTime borrowedDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        BorrowStatus status,
        // Full Book details
        BookDto book
) {}