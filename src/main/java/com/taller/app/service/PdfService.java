package com.taller.app.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

    private static final Pattern IMG_PATTERN = Pattern.compile("src=\"(images/[^\"]+)\"");
    // Cache de imágenes ya procesadas para no reescalar en cada request
    private final Map<String, String> imageCache = new HashMap<>();

    public byte[] generarPdf(String html) {
        try {
            // Strip UTF-8 BOM if present — openhtmltopdf's XML parser rejects it
            if (html.startsWith("\uFEFF")) {
                html = html.substring(1);
            }
            // Normalize doctype to uppercase for strict XML parser
            if (html.startsWith("<!doctype")) {
                html = "<!DOCTYPE" + html.substring("<!doctype".length());
            }
            long t0 = System.currentTimeMillis();
            html = embedAllImages(html);
            long t1 = System.currentTimeMillis();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            long t2 = System.currentTimeMillis();

            org.slf4j.LoggerFactory.getLogger(PdfService.class).info(
                "PDF generado — embed imágenes: {}ms, render: {}ms, total: {}ms",
                t1 - t0,
                t2 - t1,
                t2 - t0
            );

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private String embedAllImages(String html) {
        Matcher matcher = IMG_PATTERN.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String srcRef = matcher.group(1);
            String fileName = srcRef.substring("images/".length());
            String dataUri = imageCache.computeIfAbsent(fileName, this::toBase64DataUri);
            matcher.appendReplacement(sb, Matcher.quoteReplacement("src=\"" + dataUri + "\""));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String toBase64DataUri(String fileName) {
        String[] paths = { "templates/images/" + fileName, "static/images/" + fileName };
        for (String path : paths) {
            try {
                ClassPathResource resource = new ClassPathResource(path);
                if (!resource.exists()) continue;
                try (InputStream is = resource.getInputStream()) {
                    byte[] bytes = resizeIfNeeded(is, 400); // máx 400px de ancho
                    String b64 = Base64.getEncoder().encodeToString(bytes);
                    String mime = fileName.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
                    return "data:" + mime + ";base64," + b64;
                }
            } catch (Exception ignored) {}
        }
        return "";
    }

    /**
     * Redimensiona la imagen si supera maxWidth píxeles de ancho.
     * Devuelve los bytes PNG resultantes.
     */
    private byte[] resizeIfNeeded(InputStream is, int maxWidth) throws Exception {
        BufferedImage original = ImageIO.read(is);
        if (original == null) return new byte[0];

        int origW = original.getWidth();
        int origH = original.getHeight();

        if (origW <= maxWidth) {
            // Ya es pequeña, devolver como PNG sin reescalar
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(original, "png", baos);
            return baos.toByteArray();
        }

        int newW = maxWidth;
        int newH = (int) (((double) origH * maxWidth) / origW);

        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(original, 0, 0, newW, newH, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, "png", baos);
        return baos.toByteArray();
    }
}
