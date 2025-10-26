package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.borrow.BorrowedBookDto;
import com.library.dto.borrow.BorrowRequest;
import com.library.dto.borrow.BorrowingRecordDto;
import com.library.entity.BorrowStatus;
import com.library.entity.User;
import com.library.service.BorrowingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // Base path for borrowing related endpoints
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    // Endpoint to Borrow a Book
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<ApiResponse<BorrowingRecordDto>> borrowBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BorrowRequest borrowRequest, // Use the DTO
            @AuthenticationPrincipal User user // Get authenticated user details
    ) {
        BorrowingRecordDto record = borrowingService.borrowBook(
                bookId,
                user.getId(),
                borrowRequest.daysToKeep() // Access daysToKeep from the record
        );
        return new ResponseEntity<>(
                ApiResponse.success(record, "Book borrowed successfully", 201),
                HttpStatus.CREATED
        );
    }

    // Endpoint to Return a Book
    @PostMapping("/return/{bookId}")
    public ResponseEntity<ApiResponse<BorrowingRecordDto>> returnBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User user
    ) {
        BorrowingRecordDto record = borrowingService.returnBook(bookId, user.getId());
        return ResponseEntity.ok(
                ApiResponse.success(record, "Book returned successfully")
        );
    }

    // Endpoint to Get User's Borrowed Books
    @GetMapping("/user/borrowed-books")
    public ResponseEntity<ApiResponse<List<BorrowedBookDto>>> getUserBorrowedBooks(
            @RequestParam(required = false) BorrowStatus status, // Use Enum directly
            @AuthenticationPrincipal User user
    ) {
        List<BorrowedBookDto> borrowedBooks = borrowingService.getUserBorrowedBooks(user.getId(), status);
        return ResponseEntity.ok(
                ApiResponse.success(borrowedBooks, "Borrowed books retrieved successfully")
        );
    }
}