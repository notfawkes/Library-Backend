package com.library.service;

import com.library.dto.book.BookCreateRequest;
import com.library.dto.book.BookDto;
import com.library.dto.book.BookPageResponse;
import com.library.entity.Book;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // A simple mapper
    private BookDto toBookDto(Book book) {
        return new BookDto(
                book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getIsbn(), book.getPublishedYear(), book.getDescription(),
                book.getCoverUrl(), book.isAvailable(), book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }

    public BookPageResponse getAllBooks(String genre, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findByFilters(genre, search, pageable);

        Page<BookDto> bookDtoPage = bookPage.map(this::toBookDto);
        return new BookPageResponse(bookDtoPage);
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return toBookDto(book);
    }

    public BookDto createBook(BookCreateRequest request) {
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .genre(request.genre())
                .isbn(request.isbn())
                .publishedYear(request.publishedYear())
                .description(request.description())
                .coverUrl(request.coverUrl())
                .totalCopies(request.totalCopies())
                .availableCopies(request.totalCopies()) // Initially, all copies are available
                .available(true)
                .build();

        Book savedBook = bookRepository.save(book);
        return toBookDto(savedBook);
    }

    public BookDto updateBook(Long id, BookCreateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setGenre(request.genre());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        book.setDescription(request.description());
        book.setCoverUrl(request.coverUrl());
        // Note: You might need more complex logic to update availableCopies
        book.setTotalCopies(request.totalCopies());

        Book updatedBook = bookRepository.save(book);
        return toBookDto(updatedBook);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found");
        }
        // Add logic here to check if book is currently borrowed before deleting
        bookRepository.deleteById(id);
    }
}