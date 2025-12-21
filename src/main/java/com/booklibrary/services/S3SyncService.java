package com.booklibrary.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.booklibrary.entity.BookEntity;
import com.booklibrary.repository.BookRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class S3SyncService {

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private ThumbnailService thumbnailService;

    @Value("${app.aws.bucket-name}")
    private String bucketName;

    @Value("${app.aws.books-folder}")
    private String booksFolder;

    // 🔥 CONTROL SWITCH (LIVE SAFE)
    @Value("${app.sync.enabled:true}")
    private boolean syncEnabled;

    // 🔥 CONTROL THUMBNAILS
    @Value("${thumbnail.enabled:true}")
    private boolean thumbnailEnabled;

    // ⏱️ RUN EVERY 5 MIN (NOT 30 SEC)
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void syncBooksFromS3() {

        if (!syncEnabled) {
            return;
        }

        System.out.println("🔄 Syncing S3 Books...");

        ListObjectsV2Result result =
                amazonS3.listObjectsV2(bucketName, booksFolder);

        for (S3ObjectSummary obj : result.getObjectSummaries()) {

            String key = obj.getKey();
            if (!key.endsWith(".pdf")) continue;

            String fileName =
                    key.substring(key.lastIndexOf("/") + 1);

            // 🔥 FIND EXISTING BOOK (IF ANY)
            BookEntity book =
                    bookRepo.findByFileName(fileName).orElse(null);

            // ✅ IF BOOK EXISTS & COVER ALREADY PRESENT → SKIP
            if (book != null && book.getCoverUrl() != null) {
                continue;
            }

            System.out.println("📘 Processing: " + fileName);

            byte[] pdfBytes;
            try (S3Object s3Object =
                         amazonS3.getObject(bucketName, key);
                 S3ObjectInputStream input =
                         s3Object.getObjectContent()) {

                pdfBytes = toByteArray(input);

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            String coverUrl = null;
            try {
                if (thumbnailEnabled) {
                    coverUrl =
                            thumbnailService.createCoverFromPdfBytes(
                                    pdfBytes, fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 🔥 CREATE OR UPDATE BOOK
            if (book == null) {
                book = new BookEntity();
                book.setBookName(fileName.replace(".pdf", ""));
                book.setFileName(fileName);
                book.setCategory("Computer books");
                book.setPdfUrl(
                        "https://" + bucketName +
                        ".s3.ap-south-1.amazonaws.com/" + key
                );
            }

            book.setCoverUrl(coverUrl);
            bookRepo.save(book);

            System.out.println("✅ Saved / Updated: " + fileName);

            // 🔥 ONE FILE PER RUN (LIVE SAFE)
            break;
        }

        System.out.println("🎉 Sync Completed");
    }

    // ================= HELPER =================
    private byte[] toByteArray(S3ObjectInputStream input)
            throws IOException {

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        byte[] data = new byte[4096];
        int n;

        while ((n = input.read(data)) != -1) {
            buffer.write(data, 0, n);
        }
        return buffer.toByteArray();
    }
}

