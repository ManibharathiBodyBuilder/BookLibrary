package com.booklibrary.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.stereotype.Repository;

import com.booklibrary.entity.MyBookEntity;

@Repository
public interface MyBookRepository extends JpaRepository<MyBookEntity, Long>, JpaSpecificationExecutor<MyBookEntity> {
}
