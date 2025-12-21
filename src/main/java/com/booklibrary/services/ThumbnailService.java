package com.booklibrary.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
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

    @Value("${app.covers.width:300}")
    private int coverWidth; // Keep thumbnail small

    public ThumbnailService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String createCoverFromPdfBytes(byte[] pdfBytes, String fileNamePrefix) throws IOException {

        // Clean file name
        String safeName = fileNamePrefix.replaceAll("[^a-zA-Z0-9\\-_.]", "_") + ".jpg";
        String s3Key = coversFolder + safeName;

        try (InputStream is = new ByteArrayInputStream(pdfBytes);
             PDDocument document = PDDocument.load(is)) {

            PDFRenderer renderer = new PDFRenderer(document);


			BufferedImage pageImage = renderer.renderImageWithDPI(0, 144, ImageType.RGB);


            // Resize Thumbnail
            int newW = coverWidth;
            int newH = (pageImage.getHeight() * newW) / pageImage.getWidth();

            BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(pageImage, 0, 0, newW, newH, null);
            g.dispose();

            // Convert → bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            // Upload to S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.setContentLength(imageBytes.length);

            amazonS3.putObject(bucketName, s3Key, new ByteArrayInputStream(imageBytes), metadata);

            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + s3Key;
        }
    }
}
