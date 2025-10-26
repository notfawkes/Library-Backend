package com.library.service;

import com.library.dto.book.BookDto; // Assuming you have BookDto and a mapper
import com.library.dto.borrow.BorrowedBookDto;
import com.library.dto.borrow.BorrowingRecordDto;
import com.library.entity.*;
import com.library.exception.BadRequestException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowingRecordRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Automatically creates constructor for final fields
@Transactional(readOnly = true) // Default to read-only transactions
public class BorrowingService {

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    // Assuming you have a BookMapper or similar utility
    // private final BookMapper bookMapper;

    @Transactional // Override default read-only for write operations
    public BorrowingRecordDto borrowBook(Long bookId, Long userId, Integer daysToKeep) {
        // 1. Find the book and check availability
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("Book '" + book.getTitle() + "' is currently not available.");
        }

        // 2. Check if user already has an active loan for this book
        boolean alreadyBorrowed = borrowingRecordRepository.existsByBookIdAndUserIdAndStatus(
                bookId, userId, BorrowStatus.ACTIVE
        );
        if (alreadyBorrowed) {
            throw new BadRequestException("You have already borrowed this book.");
        }

        // 3. Find the user (ensure user exists)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // --- Perform borrowing ---
        // 4. Decrease book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setAvailable(false);
        }
        bookRepository.save(book); // Save changes to the book

        // 5. Create and save the borrowing record
        BorrowingRecord record = BorrowingRecord.builder()
                .user(user)
                .book(book)
                .dueDate(LocalDateTime.now().plusDays(daysToKeep != null ? daysToKeep : 30)) // Default 30 days
                .status(BorrowStatus.ACTIVE)
                .build();
        BorrowingRecord savedRecord = borrowingRecordRepository.save(record);

        return convertToDto(savedRecord);
    }

    @Transactional // Override default read-only
    public BorrowingRecordDto returnBook(Long bookId, Long userId) {
        // 1. Find the active borrowing record
        BorrowingRecord record = borrowingRecordRepository
                .findByBookIdAndUserIdAndStatus(bookId, userId, BorrowStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active borrowing record found for book ID " + bookId + " by user ID " + userId
                ));

        // --- Perform return ---
        // 2. Update the record
        record.setReturnDate(LocalDateTime.now());
        record.setStatus(BorrowStatus.RETURNED);
        // Optionally check if overdue here and set status accordingly
        // if (LocalDateTime.now().isAfter(record.getDueDate())) {
        //     record.setStatus(BorrowStatus.OVERDUE); // Decide if OVERDUE is a final state or if RETURNED overrides it
        // }
        BorrowingRecord updatedRecord = borrowingRecordRepository.save(record);

        // 3. Increase book availability
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setAvailable(true); // A returned book is always available
        bookRepository.save(book);

        return convertToDto(updatedRecord);
    }

    public List<BorrowedBookDto> getUserBorrowedBooks(Long userId, BorrowStatus status) {
        List<BorrowingRecord> records;
        if (status != null) {
            records = borrowingRecordRepository.findAllByUserIdAndStatusWithBookAndUser(userId, status);
        } else {
            records = borrowingRecordRepository.findAllByUserIdWithBookAndUser(userId);
        }

        // Convert to the detailed DTO including book info
        return records.stream()
                .map(this::convertToBorrowedBookDto)
                .collect(Collectors.toList());
    }


    // --- Helper Methods ---

    // Converts BorrowingRecord Entity to BorrowingRecordDto
    private BorrowingRecordDto convertToDto(BorrowingRecord record) {
        return new BorrowingRecordDto(
                record.getId(),
                record.getUser().getId(),
                record.getUser().getUsername(),
                record.getBook().getId(),
                record.getBook().getTitle(),
                record.getBorrowedDate(),
                record.getDueDate(),
                record.getReturnDate(),
                record.getStatus()
        );
    }

    // Converts BorrowingRecord Entity to BorrowedBookDto (includes full BookDto)
    private BorrowedBookDto convertToBorrowedBookDto(BorrowingRecord record) {
        // Assumes you have a way to convert Book entity to BookDto
        BookDto bookDto = convertBookToBookDto(record.getBook()); // Replace with your actual book mapping

        return new BorrowedBookDto(
                record.getId(),
                record.getBorrowedDate(),
                record.getDueDate(),
                record.getReturnDate(),
                record.getStatus(),
                bookDto
        );
    }

    // Placeholder: Implement your Book to BookDto mapping logic
    private BookDto convertBookToBookDto(Book book) {
        // Replace with your actual mapping logic, potentially using a library like MapStruct
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.getDescription(),
                book.getCoverUrl(),
                book.isAvailable(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}