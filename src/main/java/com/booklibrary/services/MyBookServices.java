package com.booklibrary.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.MyBookEntity;
import com.booklibrary.repository.MyBookRepository;

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
	
	
	
}
