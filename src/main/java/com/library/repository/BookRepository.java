package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Custom query for filtering by genre AND searching by title or author
    @Query("SELECT b FROM Book b WHERE " +
            "(:genre IS NULL OR b.genre = :genre) AND " +
            "(:search IS NULL OR b.title ILIKE %:search% OR b.author ILIKE %:search%)")
    Page<Book> findByFilters(
            @Param("genre") String genre,
            @Param("search") String search,
            Pageable pageable
    );
}