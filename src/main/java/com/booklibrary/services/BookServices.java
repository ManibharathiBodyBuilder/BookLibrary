package com.booklibrary.services;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.BookEntity;
//import com.booklibrary.entity.FileModel;     -----Is it for file_Repository Doc!!!
import com.booklibrary.repository.BookRepository;
//import com.booklibrary.repository.FileRepository;    ------Is it for file_Repository Doc!!!
import com.booklibrary.specificationbook.BookSpecification;


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
     
     public BookEntity getBookById(Long id) {
    	    return bookRepository.findById(id).orElse(null);
    	}
     
     public List<BookEntity> searchBooks(String name) {
         Specification<BookEntity> spec = Specification
                 .where(BookSpecification.hasBookName(name));
                // .and(BookSpecification.hasBookAuthor(author));
                // .and(BookSpecification.hasBookLanch(lanch));

         return bookRepository.findAll(spec);
     }

     

         public byte[] downloadPdf(String pdfUrl) throws IOException {

             URL url = new URL(pdfUrl);
             URLConnection connection = url.openConnection();
             connection.setConnectTimeout(5000);
             connection.setReadTimeout(5000);

             try (InputStream in = connection.getInputStream();
                  ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                 byte[] data = new byte[4096];
                 int n;
                 while ((n = in.read(data)) != -1) {
                     buffer.write(data, 0, n);
                 }
                 return buffer.toByteArray();
             }
         }
     


	

}
