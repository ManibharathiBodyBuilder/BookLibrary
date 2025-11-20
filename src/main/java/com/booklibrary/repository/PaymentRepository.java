package com.booklibrary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booklibrary.payment.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByBookIdAndStatusOrderByIdDesc(Long bookId, String status);
    Optional<Payment> findByBookIdAndDownloadToken(Long bookId, String downloadToken);
}

