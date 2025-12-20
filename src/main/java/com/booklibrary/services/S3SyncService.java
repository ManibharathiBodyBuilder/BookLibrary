package com.booklibrary.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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

    @Scheduled(cron = "*/30 * * * * *")
    public void syncBooksFromS3() {

        System.out.println("🔄 Syncing S3 Books...");

        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketName, booksFolder);

        result.getObjectSummaries().forEach(obj -> {

            String key = obj.getKey();
            if (!key.endsWith(".pdf")) return;

            String fileName = key.substring(key.lastIndexOf("/") + 1);

            boolean exists = bookRepo.existsByFileName(fileName);
            if (exists) return;

            System.out.println("📘 Processing: " + fileName);

            byte[] pdfBytes;

            try {
                S3Object s3Object = amazonS3.getObject(bucketName, key);
                pdfBytes = toByteArray(s3Object.getObjectContent());  // <-- CORRECT CALL
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try { Thread.sleep(500); } catch (Exception ignored) {}

            try {
                String coverUrl = thumbnailService.createCoverFromPdfBytes(pdfBytes, fileName);

                BookEntity book = new BookEntity();
                book.setBookName(fileName.replace(".pdf", ""));
                book.setFileName(fileName);
                book.setPdfUrl("https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + key);
                book.setCoverUrl(coverUrl);

                bookRepo.save(book);

                System.out.println("✅ Inserted: " + fileName);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        System.out.println("🎉 Sync Completed!");
    }

    // ---------- PUT THIS OUTSIDE ABOVE METHOD ----------
    private byte[] toByteArray(S3ObjectInputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int n;

        while ((n = input.read(data)) != -1) {
            buffer.write(data, 0, n);
        }
        input.close();
        return buffer.toByteArray();
    }
}
