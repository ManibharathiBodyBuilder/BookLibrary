package com.booklibrary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booklibrary.entity.PasswordResetToken;
import com.booklibrary.entity.UserEntity;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(UserEntity user);
}

