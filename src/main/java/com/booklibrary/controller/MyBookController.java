package com.booklibrary.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booklibrary.entity.BookEntity;
import com.booklibrary.entity.MyBookEntity;
import com.booklibrary.services.MyBookServices;

@Controller
public class MyBookController {
	
	@Autowired
	private MyBookServices myBookServices;
	
	@RequestMapping("/deletemylist/{BookId}")
	public String DeletedID(@PathVariable ("BookId")  Long BookId)
	{
		myBookServices.DeletbyId(BookId);
		return "redirect:/my_book";
	}
	
	@GetMapping("/mybooks/search")
	public String searchMyBooks(@RequestParam(required = false) String bookName,
	                          Model model) {

	    List<MyBookEntity> results = myBookServices.searchBooks(bookName);

	    if (results == null || results.isEmpty()) {
	        model.addAttribute("message", "⚠️ No data found for your search!");
	        results = new ArrayList<>(); // prevent null list
	    }
	    
	    

	    // ✅ Always add 'books' to the model
	    model.addAttribute("books", results);

	    // ✅ Add default pagination variables (to prevent EL1030E error)
	    model.addAttribute("currentPage", 0);
	    model.addAttribute("totalPages", 1);
	    model.addAttribute("startCount", results.isEmpty() ? 0 : 1);
	    model.addAttribute("endCount", results.size());
	    model.addAttribute("totalBooks", results.size());

	    return "mybook";
	}
	
	}
