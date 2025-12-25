package com.booklibrary.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.stereotype.Repository;

import com.booklibrary.entity.MyBookEntity;
import com.booklibrary.entity.UserEntity;

@Repository
public interface MyBookRepository extends JpaRepository<MyBookEntity, Long>, JpaSpecificationExecutor<MyBookEntity> {

	List<MyBookEntity> findByUser(UserEntity user);


    boolean existsByUserAndBookId(UserEntity user, Long bookId);
}
