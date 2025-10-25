package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.book.BookCreateRequest;
import com.library.dto.book.BookDto;
import com.library.dto.book.BookPageResponse;
import com.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<BookPageResponse>> getAllBooks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BookPageResponse response = bookService.getAllBooks(genre, search, page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Books retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getBookById(@PathVariable Long id) {
        BookDto book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(book, "Book retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") // Secures this endpoint for ADMINs
    public ResponseEntity<ApiResponse<BookDto>> createBook(
            @Valid @RequestBody BookCreateRequest request
    ) {
        BookDto createdBook = bookService.createBook(request);
        return new ResponseEntity<>(
                ApiResponse.success(createdBook, "Book created successfully", 201),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookCreateRequest request
    ) {
        BookDto updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedBook, "Book updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", 200));
    }
}