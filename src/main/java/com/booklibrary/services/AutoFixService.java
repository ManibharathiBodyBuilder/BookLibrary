package com.booklibrary.services;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.BookEntity;
import com.booklibrary.repository.BookRepository;

@Service
public class AutoFixService {

    @Autowired
    private BookRepository bookRepository;

    private final String bucketName = "booklibrary-storage";

    public void fixMissingFields() {

        List<BookEntity> books = bookRepository.findAll();

        for (BookEntity book : books) {

            // Fix file_name from pdf_url
            if (book.getFileName() == null && book.getPdfUrl() != null) {
                String pdfUrl = book.getPdfUrl();
                String fileName = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
                book.setFileName(fileName);
            }

            // If file_name still null → skip
            if (book.getFileName() == null) continue;

            // Generate clean base name
            String baseName = book.getFileName().replace(".pdf", "").trim();

            // Fix pdf_url if missing
            if (book.getPdfUrl() == null) {
                String pdfUrl = "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/pdfs/" + baseName + ".pdf";
                book.setPdfUrl(pdfUrl);
            }

            // Fix cover_url if missing
            if (book.getCoverUrl() == null) {
                String coverUrl = "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/covers/" + baseName + ".jpg";
                book.setCoverUrl(coverUrl);
            }
        }

        bookRepository.saveAll(books);
    }
}

