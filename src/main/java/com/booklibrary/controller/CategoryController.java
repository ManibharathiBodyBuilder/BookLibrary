package com.booklibrary.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.booklibrary.dto.CategoryDTO;
import com.booklibrary.entity.BookEntity;
import com.booklibrary.repository.BookRepository;

@Controller
public class CategoryController {

    @Autowired
    private BookRepository bookRepo;

    @GetMapping("/category/{category}")
    public String booksByCategory(@PathVariable String category, Model model) {

        List<BookEntity> books = bookRepo.findByCategory(category);

        model.addAttribute("category", category);
        model.addAttribute("books", books);

        return "books-by-category"; // VERY IMPORTANT
    }
    
    
    @GetMapping("/delete-category/{category}")
    public String deleteCategory(@PathVariable String category) {

        // delete all books under the category
        List<BookEntity> books = bookRepo.findByCategory(category);

        for (BookEntity b : books) {
            bookRepo.delete(b);
        }

        return "redirect:/available_books?deleted=" + category;
    }

    
    
   
}


