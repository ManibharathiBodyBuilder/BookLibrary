package com.booklibrary.controller;

import java.net.URL;
import java.time.LocalDate;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.booklibrary.dto.CategoryDTO;
import com.booklibrary.entity.BookEntity;
//import com.booklibrary.entity.FileModel; -------Is it for file_Repository Doc!!!
import com.booklibrary.entity.MyBookEntity;
import com.booklibrary.entity.UserEntity;
import com.booklibrary.repository.BookRepository;
import com.booklibrary.repository.UserRepository;
import com.booklibrary.services.BookHistoryService;
import com.booklibrary.services.BookServices;
//import com.booklibrary.services.FileServices;  --------Is it for file_Repository Doc!!!
import com.booklibrary.services.MyBookServices;
import com.booklibrary.services.ThumbnailAsyncService;
import com.booklibrary.services.ThumbnailService;
import com.booklibrary.utils.PdfThumbnailGenerator;


@Controller()
public class BookController {

	@Autowired
	private BookServices bookServices;

	@Autowired
	private MyBookServices myBookServices;
	
	@Autowired
	private BookRepository bookRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	
	@Autowired
	private BookHistoryService historyservice;

	
	@Autowired
	private AmazonS3 s3;

	@Value("${aws.s3.bucket}")
	private String bucketName;
	
	@Autowired
	private ThumbnailService thumbnailService;
	
	@Autowired
	private ThumbnailAsyncService thumbnailAsyncService;


	
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
	

	@GetMapping("/available_books")
	public String listCategories(Model model) {

	    // GET CATEGORY LIST WITH COUNT
	    List<Object[]> categoryData = bookRepo.countBooksByCategory();
	    List<CategoryDTO> categories = new ArrayList<>();

	    for (Object[] row : categoryData) {
	        String category = row[0] != null ? row[0].toString() : "Unknown";
	        int count = Integer.parseInt(row[1].toString());
	        categories.add(new CategoryDTO(category, count));
	    }

	    model.addAttribute("categories", categories);

	    return "categories";  // show category page
	}

	
/*	@GetMapping("/available_books")
	public String listBooks(@RequestParam(defaultValue = "0") int page, Model model) {

	    int size = 10;
	    Pageable pageable = PageRequest.of(page, size);

	    // FIXED: Load full BookEntity including coverUrl & pdfUrl
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

	    return "booklist";
	}*/



	/*@PostMapping("/SaveBook")
	public String addBookBulk(
	        @RequestParam("category") String category,
	        @RequestParam("files") MultipartFile[] files) throws Exception {

	    for (MultipartFile file : files) {

	        BookEntity book = new BookEntity();
	        book.setBookName(file.getOriginalFilename());
	        book.setCategory(category);

	        // 1️⃣ Save Book to get ID
	        BookEntity saved = bookRepo.save(book);

	        // 2️⃣ Upload PDF to S3
	        String pdfKey = "books/" + saved.getBookId() + "-" + file.getOriginalFilename();
	        ObjectMetadata pdfMeta = new ObjectMetadata();
	        pdfMeta.setContentLength(file.getSize());
	        pdfMeta.setContentType("application/pdf");

	        s3.putObject(bucketName, pdfKey, file.getInputStream(), pdfMeta);
	        String pdfUrl = s3.getUrl(bucketName, pdfKey).toString();
	        saved.setPdfUrl(pdfUrl);

	        // 3️⃣ Generate Thumbnail & Upload
	        BufferedImage thumb = PdfThumbnailGenerator.generateThumbnail(file.getInputStream());
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(thumb, "jpg", baos);
	        byte[] imageBytes = baos.toByteArray();

	        String imageKey = "thumbnails/" + saved.getBookId() + ".jpg";
	        ObjectMetadata imgMeta = new ObjectMetadata();
	        imgMeta.setContentLength(imageBytes.length);
	        imgMeta.setContentType("image/jpeg");

	        s3.putObject(bucketName, imageKey, new ByteArrayInputStream(imageBytes), imgMeta);
	        String imageUrl = s3.getUrl(bucketName, imageKey).toString();

	        // 4️⃣ Save URL updates
	        saved.setCoverUrl(imageUrl);
	        bookRepo.save(saved);
	    }

	    return "redirect:/available_books";
	}*/
	
	
	@PostMapping("/SaveBook")
	public String addBookBulk(
	        @RequestParam("category") String category,
	        @RequestParam("files") MultipartFile[] files) throws Exception {

	    for (MultipartFile file : files) {

	        byte[] pdfBytes = file.getBytes();

	        BookEntity book = new BookEntity();
	        book.setBookName(file.getOriginalFilename());
	        book.setCategory(category);

	        BookEntity saved = bookRepo.save(book);

	        // PDF upload
	        String pdfKey = "books/" + saved.getBookId() + "-" + file.getOriginalFilename();
	        s3.putObject(bucketName, pdfKey,
	                new ByteArrayInputStream(pdfBytes),
	                new ObjectMetadata());

	        saved.setPdfUrl(s3.getUrl(bucketName, pdfKey).toString());

	        // 🔥 MARK THUMBNAIL PENDING
	        saved.setCoverUrl("PENDING"); 
	        bookRepo.save(saved);

	        // 🔥 SEND TO BACKGROUND THREAD
	        thumbnailAsyncService.generateThumbnailAsync(
	                saved.getBookId(),
	                pdfBytes,
	                saved.getBookId() + "-" + file.getOriginalFilename()
	        );
	    }

	    return "redirect:/available_books";
	}

	
	
	@GetMapping("/admin/zip-upload")
	public String showZipUploadPage() {
	    return "admin/zipUpload";
	}

	
	
	@PostMapping("/admin/upload-zip")
	public String uploadZipBooks(
	        @RequestParam("category") String category,
	        @RequestParam("zipFile") MultipartFile zipFile) throws Exception {

	    ZipInputStream zis =
	            new ZipInputStream(zipFile.getInputStream());

	    ZipEntry entry;

	    while ((entry = zis.getNextEntry()) != null) {

	        if (!entry.getName().endsWith(".pdf")) continue;

	        byte[] pdfBytes = readZipEntryBytes(zis);


	        // 🔥 CREATE BOOK
	        BookEntity book = new BookEntity();
	        book.setBookName(entry.getName().replace(".pdf", ""));
	        book.setCategory(category);

	        BookEntity saved = bookRepo.save(book);

	        // 🔥 PDF → S3
	        String pdfKey =
	            "books/" + saved.getBookId() + "-" + entry.getName();

	        ObjectMetadata meta = new ObjectMetadata();
	        meta.setContentLength(pdfBytes.length);
	        meta.setContentType("application/pdf");

	        s3.putObject(bucketName,
	                pdfKey,
	                new ByteArrayInputStream(pdfBytes),
	                meta);

	        saved.setPdfUrl(
	            s3.getUrl(bucketName, pdfKey).toString());

	        // 🔥 TEMP COVER
	        saved.setCoverUrl("PENDING");
	        bookRepo.save(saved);

	        // 🔥 ASYNC THUMBNAIL
	        thumbnailAsyncService.generateThumbnailAsync(
	                saved.getBookId(),
	                pdfBytes,
	                saved.getBookId() + "-" + entry.getName()
	        );

	        // 🔥 SAFETY GAP
	        Thread.sleep(500);
	    }

	    zis.close();
	    return "redirect:/available_books";
	}

	
	private byte[] readZipEntryBytes(ZipInputStream zis) throws IOException {

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    byte[] data = new byte[4096];
	    int n;

	    while ((n = zis.read(data)) != -1) {
	        buffer.write(data, 0, n);
	    }

	    return buffer.toByteArray();
	}
	
	//FIX OLD BOOKS (IMPORTANT 🔥)
	
	@GetMapping("/admin/generate-old-covers")
	public String generateOldCovers() throws Exception {

	    List<BookEntity> books =
	        bookRepo.findByCoverUrl("PENDING");

	    for (BookEntity book : books) {

	    	byte[] pdfBytes =
	    			bookServices.downloadPdf(book.getPdfUrl());


	        String coverUrl =
	            thumbnailService.createCoverFromPdfBytes(
	                pdfBytes, book.getFileName());

	        book.setCoverUrl(coverUrl);
	        bookRepo.save(book);

	        Thread.sleep(2000); // LIVE SAFE
	    }

	    return "DONE";
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

	    Authentication auth =
	        SecurityContextHolder.getContext().getAuthentication();

	    if (auth == null || auth.getName().equals("anonymousUser")) {
	        return "redirect:/login";
	    }

	    String username = auth.getName();

	    UserEntity user = bookRepo
	            .findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    List<MyBookEntity> list1 =
	            myBookServices.getMyBooksByUser(user);

	    model.addAttribute("books", list1);
	    return "mybook";
	}




	@RequestMapping("/mylist/{bookId}")
	public String addToMyBook(@PathVariable Long bookId) {

	    // 1️⃣ get logged-in user
	    Authentication auth =
	            SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    UserEntity user = userRepo.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    // 2️⃣ get book
	    BookEntity book = bookServices.findBydata(bookId);

	    // 3️⃣ save user-wise mybook
	    MyBookEntity myBook = new MyBookEntity();
	    myBook.setUser(user);                 // 🔥 IMPORTANT
	    myBook.setBookId(book.getBookId());
	    myBook.setBookName(book.getBookName());

	    myBookServices.saveMyBook(myBook);

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
	public ResponseEntity<?> getFile(@PathVariable("id") Long id) {

	    // record download history
	    try {
	        historyservice.downloadBook(id.intValue());
	    } catch (Exception e) {
	        System.out.println(">> Error saving download history: " + e.getMessage());
	    }

	    BookEntity book = bookServices.getBookById(id);
	    if (book == null || book.getPdfUrl() == null) {
	        return ResponseEntity.notFound().build();
	    }

	    //  NEW: Redirect user to S3 PDF URL
	    return ResponseEntity.status(HttpStatus.FOUND)
	            .header(HttpHeaders.LOCATION, book.getPdfUrl())
	            .build();
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

	    return "books-by-category";
	}


	
	/*@GetMapping("/dashboard")
	public String dashboard(Model model) {

	    long totalBooks = bookRepo.count();
	    long totalCategories = bookRepo.countDistinctCategories();
	    List<Object[]> booksByCategory = bookRepo.countBooksByCategory();

	    model.addAttribute("totalBooks", totalBooks);
	    model.addAttribute("totalCategories", totalCategories);
	    model.addAttribute("booksByCategory", booksByCategory);

	    return "dashboard";
	}*/
	
	@GetMapping("/dashboard")
	public String dashboard(Model model) {

	    long totalBooks = bookRepo.count();
	    long totalCategories = bookRepo.countDistinctCategories();
	    List<Object[]> booksByCategory = bookRepo.countBooksByCategory();

	    // Convert category & count into separate arrays
	    List<String> categoryNames = new ArrayList<>();
	    List<Long> categoryCounts = new ArrayList<>();

	    for (Object[] row : booksByCategory) {
	        categoryNames.add((String) row[0]);
	        categoryCounts.add((Long) row[1]);
	    }

	    model.addAttribute("totalBooks", totalBooks);
	    model.addAttribute("totalCategories", totalCategories);
	    model.addAttribute("categoryNames", categoryNames);
	    model.addAttribute("categoryCounts", categoryCounts);

	    return "dashboard";
	}



	 
/*	 @GetMapping("/readbook/{id}")
	 public ResponseEntity<?> readBook(@PathVariable Long id) {
	     BookEntity book = bookRepo.findById(id)
	             .orElseThrow(() -> new RuntimeException("Book not found"));

	     if (book.getPdfUrl() == null) {
	         throw new RuntimeException("No PDF URL found for this book");
	     }

	     // Inline open PDF from S3
	     return ResponseEntity.status(HttpStatus.FOUND)
	             .header(HttpHeaders.LOCATION, book.getPdfUrl())
	             .build();
	 }*/
	
	
	@GetMapping("/api/pdf/{id}")
	public ResponseEntity<byte[]> getPdf(@PathVariable Long id) throws Exception {

	    BookEntity book = bookRepo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Book not found"));

	    String pdfUrl = book.getPdfUrl();
	    if (pdfUrl == null) {
	        throw new RuntimeException("PDF URL missing");
	    }

	    URL url = new URL(pdfUrl);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    try (InputStream is = url.openStream()) {
	        byte[] buffer = new byte[8192];
	        int bytesRead;
	        while ((bytesRead = is.read(buffer)) != -1) {
	            baos.write(buffer, 0, bytesRead);
	        }
	    }

	    return ResponseEntity.ok()
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(baos.toByteArray());
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
	 
	 @PostMapping("/addBook")
	 public String addBook(@ModelAttribute BookEntity book,
	                       RedirectAttributes redirectAttributes) {

	     bookRepo.save(book);

	     // Flash message (one-time)
	     redirectAttributes.addFlashAttribute("newBookMessage",
	             "🎉 New book added successfully!");

	     return "redirect:/testing";
	 }
	 
	
/*	 @GetMapping("/testing")
	 public String GetData(Model model) {

	     LocalDate today = LocalDate.now();

	     // Get today's books
	     List<BookEntity> todayBooks = bookRepo.findByCreatedDate(today);

	     model.addAttribute("todayBooks", todayBooks);

	     return "testing";
	 }*/









}
