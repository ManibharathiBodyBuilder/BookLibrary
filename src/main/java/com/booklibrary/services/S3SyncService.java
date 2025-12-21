package com.booklibrary.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.booklibrary.entity.BookEntity;
import com.booklibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class S3SyncService {

    private final AmazonS3 amazonS3;
    private final BookRepository bookRepo;
    private final ThumbnailService thumbnailService;

    @Value("${app.aws.bucket-name}")
    private String bucketName;

    @Value("${app.aws.books-folder}")
    private String booksFolder;

    @Value("${app.sync.enabled:false}")
    private boolean syncEnabled;

    @Value("${thumbnail.enabled:false}")
    private boolean thumbnailEnabled;

    public S3SyncService(AmazonS3 amazonS3,
                         BookRepository bookRepo,
                         ThumbnailService thumbnailService) {
        this.amazonS3 = amazonS3;
        this.bookRepo = bookRepo;
        this.thumbnailService = thumbnailService;
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void syncBooksFromS3() {

        if (!syncEnabled) {
            return; // 🔒 LIVE SAFE
        }

        System.out.println("🔄 Syncing S3 Books...");

        ListObjectsV2Result result =
                amazonS3.listObjectsV2(bucketName, booksFolder);

        for (S3ObjectSummary obj : result.getObjectSummaries()) {

            String key = obj.getKey();
            if (!key.endsWith(".pdf")) continue;

            String fileName =
                    key.substring(key.lastIndexOf("/") + 1);

            if (bookRepo.existsByFileName(fileName)) continue;

            byte[] pdfBytes;

            try (S3Object s3Object =
                         amazonS3.getObject(bucketName, key);
                 S3ObjectInputStream input =
                         s3Object.getObjectContent()) {

                pdfBytes = toByteArray(input);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            String coverUrl = null;
            try {
                if (thumbnailEnabled) {
                    coverUrl =
                        thumbnailService.createCoverFromPdfBytes(pdfBytes, fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            BookEntity book = new BookEntity();
            book.setBookName(fileName.replace(".pdf", ""));
            book.setFileName(fileName);
            book.setCategory("Computer books"); // ✅ FIX
            book.setPdfUrl("https://" + bucketName +
                    ".s3.ap-south-1.amazonaws.com/" + key);
            book.setCoverUrl(coverUrl);

            bookRepo.save(book);

            System.out.println("✅ Inserted: " + fileName);

            break; // 🔥 ONLY ONE PDF PER RUN
        }

        System.out.println("🎉 Sync Completed");
    }

    private byte[] toByteArray(S3ObjectInputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int n;
        while ((n = input.read(data)) != -1) {
            buffer.write(data, 0, n);
        }
        return buffer.toByteArray();
    }
}
