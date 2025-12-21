package com.booklibrary.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.booklibrary.entity.BookEntity;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {

	
	boolean existsByFileName(String fileName);
	
	List<BookEntity> findByCategory(String category);
	

	    Optional<BookEntity> findByFileName(String fileName);


	
	// 1️⃣ Count total categories
	@Query("SELECT COUNT(DISTINCT b.category) FROM BookEntity b")
	long countDistinctCategories();


	
	@Query("SELECT b.category, COUNT(b) FROM BookEntity b WHERE b.category IS NOT NULL AND b.category <> '' GROUP BY b.category ORDER BY COUNT(b) DESC")
	        
	    
	List<Object[]> countBooksByCategory();





	@Query("SELECT new com.booklibrary.entity.BookEntity(b.bookId, b.bookName, b.fileName) FROM BookEntity b")
	Page<BookEntity> findLight(Pageable pageable);



	
	@Query("SELECT b FROM BookEntity b WHERE b.createdDate = :today")
	List<BookEntity> findByCreatedDate(@Param("today") LocalDate today);


	

}
