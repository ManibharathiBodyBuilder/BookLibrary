package com.booklibrary.cloudconfig;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.util.Arrays;

@Configuration
@Profile("prod")
public class DriveConfig {

    @Value("${app.google.drive.service-account.key-path}")
    private String keyPath;

    @Bean
    public Drive googleDrive() throws Exception {

        // Load service account from file system path
        GoogleCredentials credentials =
                GoogleCredentials.fromStream(new FileInputStream(keyPath))
                        .createScoped(Arrays.asList(DriveScopes.DRIVE_READONLY));

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        return new Drive.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName("BookLibraryAutoSync")
                .build();
    }
}
