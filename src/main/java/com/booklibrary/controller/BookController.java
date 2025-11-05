package com.booklibrary.controller;

import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.booklibrary.entity.BookEntity;
//import com.booklibrary.entity.FileModel; -------Is it for file_Repository Doc!!!
import com.booklibrary.entity.MyBookEntity;
import com.booklibrary.services.BookServices;
//import com.booklibrary.services.FileServices;  --------Is it for file_Repository Doc!!!
import com.booklibrary.services.MyBookServices;


@Controller()
public class BookController {

	@Autowired
	private BookServices bookServices;

	@Autowired
	private MyBookServices myBookServices;
	
	/*@Autowired
	private FileServices fileServices;
*/
	@GetMapping("/")
	public String login() {
		return "login";
	}

	@GetMapping("/test")
	public String GetData() {
		return "testing";
	}

	@GetMapping("/book_register")
	public String BookReg() {
		return "bookRegister";
	}
	@GetMapping("/logout")
	public String logout() {
		return "logout";
	}
	@GetMapping("/loggedout")
	public String loggedout() {
		return "loggedout";
	}
	@GetMapping("/Verified")
	public String Verify()
	{
		return "Verified";
	}
	
	@GetMapping("/available_books")
	public ModelAndView BookAllBook() {
		List<BookEntity> list = bookServices.GetAllBook();
		return new ModelAndView("booklist", "book", list);
	}

	@PostMapping("/SaveBook")
	public String AddBooK(@ModelAttribute   BookEntity b) {
		bookServices.SaveBook(b);
		return "redirect:/available_books"; 

	}
	//----------Is it for file_Repository Doc!!!
	/*@PostMapping("/ProcessUpload")
	public String  ProcessUpload(@RequestParam MultipartFile file,Model model)throws IOException
	{
		byte[] content= file.getBytes();
		String name = file.getName();
		String fileType = file.getContentType();
		FileModel fileModel = new FileModel(content,name,fileType);
		fileServices.UploadDoc(fileModel);
		
		model.addAttribute("allFiles", fileServices.GetAllFile());
		return "redirect:/available_books";
	}
	
	@GetMapping("/{fileId}")
	public ResponseEntity<byte[]> getFile(@PathVariable Long fileId)
	{
		FileModel fm = fileServices.GetById(fileId);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_TYPE,fm.getFileType());
		httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+fm.getName());
		return ResponseEntity.ok()
				.headers(httpHeaders).body(fm.getContent());
	}
	---------------Is it for file_Repository Doc!!!
*/
	@GetMapping("/my_book")
	public String GetAllMyBook(Model model) {
		List<MyBookEntity> list1 = myBookServices.GetAllMyBook();
		model.addAttribute("book", list1);
		return "mybook";
	}

	@RequestMapping("/mylist/{BookId}")
	public String GetId(@PathVariable("BookId") Long bookId) {

		BookEntity bookEntity = bookServices.findBydata(bookId);
		MyBookEntity mbe = new MyBookEntity(bookEntity.getBookId(), bookEntity.getBookName(),
				bookEntity.getBookAuthor(), bookEntity.getBookLanch());
		myBookServices.saveMyBook(mbe);
		return "redirect:/my_book";
	}

	@RequestMapping("/editbook/{BookId}")
	public String EditBook(@PathVariable("BookId") Long bookId, Model model) {
		BookEntity b = bookServices.findBydata(bookId);
		model.addAttribute("book", b);
		return "bookEdit";

	}

	@RequestMapping("/deletebook/{BookId}")
	public String DeleteByIdes(@PathVariable("BookId") Long bookId) {
		bookServices.DeleteByID(bookId);
		return "redirect:/available_books";

	}

	@GetMapping("/getFile")
	public String downloadfile(HttpServletResponse reponse) throws IOException {
		File file = new File("src\\java_tutorial.pdf");
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
		return "redirect:/available_books";

	}

}
