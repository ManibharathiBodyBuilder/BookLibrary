package com.booklibrary.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.booklibrary.entity.BookEntity;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {

	

	@Query("SELECT COUNT(DISTINCT b.bookName) FROM BookEntity b")
	long findDistinctAuthorCount();


	@Query("SELECT b.bookYear, COUNT(b) FROM BookEntity b GROUP BY b.bookYear")
	List<Object[]> countBooksByYear();



	@Query("SELECT new com.booklibrary.entity.BookEntity(b.bookId, b.bookName, b.fileName) FROM BookEntity b")
	Page<BookEntity> findLight(Pageable pageable);





	

}
