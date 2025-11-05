package com.booklibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booklibrary.entity.BookEntity;
//import com.booklibrary.entity.FileModel;    Is it for file_Repository Doc!!!

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>{

	

}
