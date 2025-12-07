package com.booklibrary.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PdfThumbnailGenerator {

    public static BufferedImage generateThumbnail(InputStream pdfStream) throws IOException {

        // Load PDF
        PDDocument document = PDDocument.load(pdfStream);

        // High quality render → **300 DPI**
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage originalImage = renderer.renderImageWithDPI(0, 300);

        document.close();

        // Resize width = 400px (same like freetamilebooks)
        int newWidth = 400;
        int newHeight = (originalImage.getHeight() * newWidth) / originalImage.getWidth();

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resized;
    }
}




