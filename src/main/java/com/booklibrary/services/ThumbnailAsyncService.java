package com.booklibrary.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.BookEntity;
import com.booklibrary.repository.BookRepository;

@Service
public class ThumbnailAsyncService {

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private BookRepository bookRepo;

    @Async
    public void generateThumbnailAsync(
            Long bookId,
            byte[] pdfBytes,
            String fileName) {

        try {
            // 🔥 Generate thumbnail
            String coverUrl =
                thumbnailService.createCoverFromPdfBytes(
                        pdfBytes, fileName);

            BookEntity book = bookRepo.findById(bookId).orElse(null);
            if (book == null) {
                return;
            }


            book.setCoverUrl(coverUrl);
            bookRepo.save(book);

            System.out.println("✅ Thumbnail done for bookId = " + bookId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
