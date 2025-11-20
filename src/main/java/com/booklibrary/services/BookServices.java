package com.booklibrary.services;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.BookEntity;
//import com.booklibrary.entity.FileModel;     -----Is it for file_Repository Doc!!!
import com.booklibrary.repository.BookRepository;
//import com.booklibrary.repository.FileRepository;    ------Is it for file_Repository Doc!!!
import com.booklibrary.specificationbook.BookSpecification;


@Service
public class BookServices {
	
	@Autowired
	private BookRepository bookRepository;
	
	
	public void SaveBook(BookEntity b)
	{
		bookRepository.save(b);
	}
	
	public List<BookEntity> GetAllBook()
	{
		return bookRepository.findAll();
		
	}

	public BookEntity findBydata(Long bookId) {
		
		return bookRepository.findById(bookId).get();
	}
     public void DeleteByID(Long bookId)
     {
    	 bookRepository.deleteById(bookId);
     }
     
     public BookEntity getBookById(Long id) {
    	    return bookRepository.findById(id).orElse(null);
    	}
     
     public List<BookEntity> searchBooks(String name) {
         Specification<BookEntity> spec = Specification
                 .where(BookSpecification.hasBookName(name));
                // .and(BookSpecification.hasBookAuthor(author));
                // .and(BookSpecification.hasBookLanch(lanch));

         return bookRepository.findAll(spec);
     }


	

}
