package com.library.repository;

import com.library.entity.BorrowStatus;
import com.library.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    // Check if a user has an active loan for a specific book
    boolean existsByBookIdAndUserIdAndStatus(Long bookId, Long userId, BorrowStatus status);

    // Find an active borrowing record
    Optional<BorrowingRecord> findByBookIdAndUserIdAndStatus(Long bookId, Long userId, BorrowStatus status);

    // Find all records for a user, optionally filtering by status
    List<BorrowingRecord> findByUserId(Long userId);
    List<BorrowingRecord> findByUserIdAndStatus(Long userId, BorrowStatus status);

    // Join fetch book and user details to avoid N+1 queries when getting borrowed books
    @Query("SELECT br FROM BorrowingRecord br JOIN FETCH br.book b JOIN FETCH br.user u WHERE u.id = :userId")
    List<BorrowingRecord> findAllByUserIdWithBookAndUser(@Param("userId") Long userId);

    @Query("SELECT br FROM BorrowingRecord br JOIN FETCH br.book b JOIN FETCH br.user u WHERE u.id = :userId AND br.status = :status")
    List<BorrowingRecord> findAllByUserIdAndStatusWithBookAndUser(@Param("userId") Long userId, @Param("status") BorrowStatus status);

}