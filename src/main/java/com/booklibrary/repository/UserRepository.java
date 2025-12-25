package com.booklibrary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booklibrary.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByFullName(String fullName);
	
	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByUsername(String username);


}

