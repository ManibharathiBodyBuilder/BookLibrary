package com.booklibrary.services;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.BookEntity;
//import com.booklibrary.entity.FileModel;     -----Is it for file_Repository Doc!!!
import com.booklibrary.repository.BookRepository;
//import com.booklibrary.repository.FileRepository;    ------Is it for file_Repository Doc!!!


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

	

}
