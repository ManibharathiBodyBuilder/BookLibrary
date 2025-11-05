package com.booklibrary.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	@GetMapping("/getFiledownload")
	public String downloadfile(HttpServletResponse reponse) throws IOException {
		File file = new File("src\\python_tutorial.pdf");
		reponse.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValues = "attachment; filename=" + file.getName();
		reponse.setHeader(headerKey, headerValues);
		ServletOutputStream outputStream = reponse.getOutputStream();
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[8192];
		int bytesRead = -1;

		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		inputStream.close();
		outputStream.close();
		return "redirect:/my_book";

}
}
