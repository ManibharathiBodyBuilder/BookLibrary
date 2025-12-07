package com.booklibrary.sync;

import com.booklibrary.entity.BookEntity;
import com.booklibrary.repository.BookRepository;
import com.booklibrary.services.ThumbnailService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Profile("dev")
public class S3SyncJob {

    private final BookRepository bookRepository;
    private final AmazonS3 amazonS3;
    private final ThumbnailService thumbnailService;

    @Value("${app.aws.bucket-name}")
    private String bucketName;

    @Value("${app.aws.books-folder:books/}")
    private String booksFolder;

    @Value("${app.sync.enabled:true}")
    private boolean syncEnabled;

    public S3SyncJob(BookRepository bookRepository,
                              AmazonS3 amazonS3,
                              ThumbnailService thumbnailService) {
        this.bookRepository = bookRepository;
        this.amazonS3 = amazonS3;
        this.thumbnailService = thumbnailService;
    }

    @Scheduled(fixedRate = 300000) // 5 min
    public void syncIfEmpty() throws IOException {

        if (!syncEnabled) {
            System.out.println("Sync disabled");
            return;
        }

        long count = bookRepository.count();
        if (count > 0) {
            System.out.println("Table already has data, skipping sync.");
            return;
        }

        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(booksFolder);

        ListObjectsV2Result result = amazonS3.listObjectsV2(req);

        if (result.getKeyCount() == 0) {
            System.out.println("No PDF files found in S3 folder.");
            return;
        }

        result.getObjectSummaries().forEach(s3ObjectSummary -> {
            String key = s3ObjectSummary.getKey();

            if (!key.endsWith(".pdf")) {
                return; // skip non-PDF files
            }

            try {
                // Download PDF as bytes
                S3Object s3object = amazonS3.getObject(bucketName, key);
                InputStream inputStream = s3object.getObjectContent();

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] temp = new byte[4096];
                int read;

                while ((read = inputStream.read(temp)) != -1) {
                    buffer.write(temp, 0, read);
                }

                byte[] pdfBytes = buffer.toByteArray();

                // Create book entry
                BookEntity entity = new BookEntity();
                String fileName = key.replace(booksFolder, "");

                entity.setBookName(fileName);
                entity.setFileName(fileName);

                // Create cover from PDF bytes
                try {
                    String safePrefix = "book_" + System.currentTimeMillis() + "_" + fileName;
                    String coverUrl = thumbnailService.createCoverFromPdfBytes(pdfBytes, safePrefix);
                    entity.setCoverUrl(coverUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                bookRepository.save(entity);
                System.out.println("Inserted from S3: " + fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}
