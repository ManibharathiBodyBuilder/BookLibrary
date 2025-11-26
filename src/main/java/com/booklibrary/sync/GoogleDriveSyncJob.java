package com.booklibrary.sync;

import com.booklibrary.entity.BookEntity;
import com.booklibrary.repository.BookRepository;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@Profile("prod")
public class GoogleDriveSyncJob {

    private final BookRepository bookRepository;
    private final Drive drive;

    @Value("${app.google.drive.folder-id}")
    private String folderId;

    @Value("${app.sync.enabled:true}")
    private boolean syncEnabled;

    // ✔ Constructor Injection (Spring recommended)
    public GoogleDriveSyncJob(BookRepository bookRepository, Drive drive) {
        this.bookRepository = bookRepository;
        this.drive = drive;
    }

    // Runs every 3 seconds (testing mode)
    @Scheduled(fixedRate = 3000)
    public void syncIfEmpty() throws IOException {

        if (!syncEnabled) {
            System.out.println("🔕 Sync disabled in dev mode");
            return;
        }

        long count = bookRepository.count();
        if (count > 0) {
            System.out.println("✅ BookEntity table already has data, skipping sync.");
            return;
        }

        System.out.println("⚙️ Table empty. Fetching books from Google Drive...");

        // Step 1: Query folder contents
        String query = String.format("'%s' in parents and trashed = false", folderId);
        FileList result = drive.files().list()
                .setQ(query)
                .setFields("files(id, name, mimeType)")
                .execute();

        List<File> files = result.getFiles();

        if (files == null || files.isEmpty()) {
            System.out.println("⚠️ No files found in Drive folder.");
            return;
        }

        // Step 2: Download and save each file
        for (File driveFile : files) {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            drive.files().get(driveFile.getId()).executeMediaAndDownloadTo(outputStream);

            BookEntity entity = new BookEntity();
            entity.setBookName(driveFile.getName());
            entity.setBookDocument(outputStream.toByteArray());
            entity.setFileName(driveFile.getName());

            bookRepository.save(entity);

            System.out.println("📥 Inserted: " + driveFile.getName());
        }

        System.out.println("✅ Sync completed successfully.");
    }
}
