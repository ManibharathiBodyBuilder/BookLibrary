package com.booklibrary.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.booklibrary.entity.BookHistory;
import com.booklibrary.entity.UserEntity;
import com.booklibrary.repository.BookHistoryRepo;
import com.booklibrary.repository.UserRepository;

@Controller
public class UserHistoryController {
	

	    @Autowired
	    private BookHistoryRepo historyRepo;
	    
	    @Autowired
	    private  UserRepository userRepo;

	    @GetMapping("/history")
	    public String history(Model model) {

	        String username = SecurityContextHolder.getContext().getAuthentication().getName();
	        UserEntity user = userRepo.findByEmail(username).orElse(null);

	        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());

	        List<BookHistory> history;
	        long totalCount;

	        if (isAdmin) {
	            history = historyRepo.findAll();
	            totalCount = historyRepo.count();  // total history count
	        } else {
	            history = historyRepo.findByUserEmailOrderByActionTimeDesc(username);
	            totalCount = historyRepo.countByUserEmail(username); // user history count
	        }

	        model.addAttribute("history", history);
	        model.addAttribute("totalCount", totalCount);
	        model.addAttribute("isAdmin", isAdmin);

	        return "history";
	    }
	    
	    	    
	   



	}



