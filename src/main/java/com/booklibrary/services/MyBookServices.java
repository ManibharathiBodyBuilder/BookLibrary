package com.booklibrary.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.BookEntity;
import com.booklibrary.entity.MyBookEntity;
import com.booklibrary.repository.MyBookRepository;
import com.booklibrary.specificationbook.BookSpecification;

@Service
public class MyBookServices {
	
	@Autowired
	private MyBookRepository myBookRepository;
	
	public void saveMyBook(MyBookEntity mb)
	{
		myBookRepository.save(mb);
	}
	
	public List<MyBookEntity> GetAllMyBook()
	{
		return myBookRepository.findAll();
	}
	
public void DeletbyId(Long BookId)
{
	myBookRepository.deleteById(BookId);
}

public List<MyBookEntity> searchBooks(String name) {
    Specification<MyBookEntity> spec =
        Specification.where(BookSpecification.hasMyBookNames(name));

    return myBookRepository.findAll(spec);
}
	
	
	
}
