package com.booklibrary.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.booklibrary.entity.BookHistory;


public interface BookHistoryRepo extends JpaRepository<BookHistory, Integer> {

    java.util.List<BookHistory>  findByUserEmailOrderByActionTimeDesc(String userEmail);
    

    long count(); // total history count
    long countByUserEmail(String email);


}


