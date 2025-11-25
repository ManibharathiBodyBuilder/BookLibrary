package com.booklibrary.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;



import com.booklibrary.entity.BookEntity;
//import com.booklibrary.entity.FileModel; -------Is it for file_Repository Doc!!!
import com.booklibrary.entity.MyBookEntity;
import com.booklibrary.repository.BookRepository;
import com.booklibrary.services.BookHistoryService;
import com.booklibrary.services.BookServices;
//import com.booklibrary.services.FileServices;  --------Is it for file_Repository Doc!!!
import com.booklibrary.services.MyBookServices;


@Controller()
public class BookController {

	@Autowired
	private BookServices bookServices;

	@Autowired
	private MyBookServices myBookServices;
	
	@Autowired
	private BookRepository bookRepo;
	
	
	@Autowired
	private BookHistoryService historyservice;
	
	/*@Autowired
	private FileServices fileServices; */
	
/*	
	@GetMapping("/")
	public String login() {
		return "login";
	}*/
	
/*	@GetMapping("/login")
	public String loginPage() {
	    return "login";
	}*/

	

	@GetMapping("/testing")
	public String GetData() {
		return "testing";
	}
	
	@GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // file name without .html
    }
	

	@GetMapping("/book_register")
	public String BookReg() {
		return "bookRegister";
	}
/*	@GetMapping("/logout")
	public String logout() {
		return "logout";
	}*/
	@GetMapping("/loggedout")
	public String loggedout() {
		return "loggedout";
	}
	@GetMapping("/Verified")
	public String Verify()
	{
		return "Verified";
	}
	

/*	@GetMapping("/available_books")
	public String getAllBooks(Model model) {
	    List<BookEntity> books = bookServices.GetAllBook();

	    if (books == null || books.isEmpty()) {
	        model.addAttribute("msg", "No books available right now!");
	    }

	    model.addAttribute("books", books);
	    return "booklist";  // ✅ always return this view
	}*/
	@GetMapping("/available_books")
	public String listBooks(@RequestParam(defaultValue = "0") int page, Model model) {

	    int size = 10;
	    Pageable pageable = PageRequest.of(page, size);
	    Page<BookEntity> bookPage = bookRepo.findAll(pageable);

	    if (bookPage.isEmpty()) {
	        model.addAttribute("msg", "No books available right now!");
	    }

	    model.addAttribute("books", bookPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", bookPage.getTotalPages());
	    model.addAttribute("totalBooks", bookPage.getTotalElements());
	    model.addAttribute("pageSize", size);

	    int startCount = page * size + 1;
	    long endCount = Math.min((page + 1) * size, bookPage.getTotalElements());
	    model.addAttribute("startCount", startCount);
	    model.addAttribute("endCount", endCount);

	    return "available_books";  // ✅ FIXED
	}


	
	
	


	@PostMapping("/SaveBook")
	public String addBook(@RequestParam("BookName") String bookName,                   
	                      @RequestParam(value = "BookDocument", required = false) MultipartFile file) throws IOException {

	    BookEntity book = new BookEntity();
	    book.setBookName(bookName);

	    // ✅ Optional file handling — avoids crash if no file uploaded
	    if (file != null && !file.isEmpty()) {
	        book.setBookDocument(file.getBytes());
	        book.setFileName(file.getOriginalFilename());
	    } else {
	        book.setBookDocument(null);
	        book.setFileName(null);
	    }

	    // ✅ Save safely
	    bookServices.SaveBook(book);

	    // ✅ Redirect to list page
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

	    model.addAttribute("books", list1);   // FIXED
	    return "mybook";
	}



	@RequestMapping("/mylist/{BookId}")
	public String GetId(@PathVariable("BookId") Long bookId) {

	    BookEntity bookEntity = bookServices.findBydata(bookId);

	    MyBookEntity mbe = new MyBookEntity();  // don't set ID!
	    mbe.setBookName(bookEntity.getBookName());

	    myBookServices.saveMyBook(mbe);

	    historyservice.addToMyBook(bookId.intValue());

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
	
	@GetMapping("/getFile/{id}")
	public ResponseEntity<byte[]> getFile(@PathVariable("id") Long id) {
	    // record download history
	    try {
	        historyservice.downloadBook(id.intValue());
	    } catch (Exception e) {
	        System.out.println(">> Error saving download history: " + e.getMessage());
	    }

	    BookEntity book = bookServices.getBookById(id);
	    if (book == null || book.getBookDocument() == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + book.getFileName() + "\"")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(book.getBookDocument());
	}



/*	@GetMapping("/getFile/{id}")
	public ResponseEntity<byte[]> getFile(@PathVariable("id") Long id) {
	    BookEntity book = bookServices.getBookById(id);
	    if (book == null || book.getBookDocument() == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + book.getFileName() + "\"")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(book.getBookDocument());
	}*/
	
	@GetMapping("/search")
	public String searchBooks(@RequestParam(required = false) String bookName,
	                          Model model) {

	    List<BookEntity> results = bookServices.searchBooks(bookName);

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

	    return "booklist";
	}


	
	 @GetMapping("/dashboard")
	    public String dashboard(Model model) {
	        long totalBooks = bookRepo.count();
	        long totalAuthors = bookRepo.findDistinctAuthorCount();
	        List<Object[]> booksByYear = bookRepo.countBooksByYear();

	        model.addAttribute("totalBooks", totalBooks);
	        model.addAttribute("totalAuthors", totalAuthors);
	        model.addAttribute("booksByYear", booksByYear);

	        return "dashboard";
	    }
	 
	 @GetMapping("/readbook/{id}")
	 public ResponseEntity<byte[]> readBook(@PathVariable Long id) {
	     BookEntity book = bookRepo.findById(id)
	             .orElseThrow(() -> new RuntimeException("Book not found"));

	     byte[] fileData = book.getBookDocument();
	     if (fileData == null) {
	         throw new RuntimeException("No document found for this book");
	     }

	     HttpHeaders headers = new HttpHeaders();
	     headers.setContentType(MediaType.APPLICATION_PDF);
	     headers.setContentDisposition(ContentDisposition.inline()
	             .filename(book.getFileName())
	             .build());

	     return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
	 }

	 
	 
	 @GetMapping("/readbookpage/{id}")
	 public String openReadPage(@PathVariable Long id, Model model) {
		 historyservice.readBook(id.intValue());
	     model.addAttribute("bookId", id);
	     return "readbook";  
	 }

	 
/*	 @GetMapping("/readbookpage/{id}")
	 public ResponseEntity<byte[]> readBook(@PathVariable Long id) {

	     // ⭐ RECORD HISTORY (SAFE - does NOT affect download)
		 historyservice.readBook(id.intValue());

	     BookEntity book = bookRepo.findById(id)
	             .orElseThrow(() -> new RuntimeException("Book not found"));

	     byte[] fileData = book.getBookDocument();
	     if (fileData == null) {
	         throw new RuntimeException("No document found for this book");
	     }

	     HttpHeaders headers = new HttpHeaders();
	     headers.setContentType(MediaType.APPLICATION_PDF);
	     headers.setContentDisposition(ContentDisposition.inline()
	             .filename(book.getFileName())
	             .build());

	     return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
	 }*/









}
