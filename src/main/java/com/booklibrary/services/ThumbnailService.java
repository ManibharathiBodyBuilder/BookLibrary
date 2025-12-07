package com.booklibrary.services;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class ThumbnailService {

    private final AmazonS3 amazonS3;
    
    @Value("${aws.region}")
    private String region;

    @Value("${app.aws.bucket-name}")
    private String bucketName;

    @Value("${app.aws.covers-folder:covers/}")
    private String coversFolder;

    @Value("${app.covers.width:400}")
    private int coverWidth;

    public ThumbnailService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String createCoverFromPdfBytes(byte[] pdfBytes, String fileNamePrefix) throws IOException {

        // Clean file name
        String safeName = fileNamePrefix.replaceAll("[^a-zA-Z0-9\\-_.]", "_") + ".jpg";
        String s3Key = coversFolder + safeName;

        // Convert PDF first page → image
        try (InputStream is = new ByteArrayInputStream(pdfBytes);
             PDDocument document = PDDocument.load(is)) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage pageImage = pdfRenderer.renderImageWithDPI(0, 150);

            // Resize to width = coverWidth
            int newW = coverWidth;
            int newH = (pageImage.getHeight() * newW) / pageImage.getWidth();

            BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
            resized.getGraphics().drawImage(pageImage, 0, 0, newW, newH, null);

            // Convert BufferedImage → ByteArray
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            // Upload to S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.setContentLength(imageBytes.length);

            ByteArrayInputStream uploadStream = new ByteArrayInputStream(imageBytes);

            amazonS3.putObject(bucketName, s3Key, uploadStream, metadata);

            // Return public S3 URL
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + s3Key;
        }
    }
}
