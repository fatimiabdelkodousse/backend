package com.app.service;

import com.app.entity.Image;
import com.app.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ✅ قائمة الامتدادات المسموح بها
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif"
    );

    // ✅ قائمة الـ Content Types المسموح بها
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/bmp",
            "image/heic",
            "image/heif",
            "application/octet-stream" // ← بعض الأجهزة ترسل هذا
    );

    public Image uploadImage(MultipartFile file, String uploadedBy) throws IOException {

        // ✅ التحقق أن الملف مش فاضي
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload");
        }

        LocalDate today = LocalDate.now();

        // ✅ التحقق من أن الأدمن لم يرفع صورة اليوم
        if (imageRepository.existsByUploadDate(today)) {
            throw new RuntimeException("An image has already been uploaded today");
        }

        // ✅ استخراج الامتداد من اسم الملف
        String originalFileName = file.getOriginalFilename();
        String extension = extractExtension(originalFileName);

        // ✅ التحقق من الامتداد أولاً (أكثر موثوقية من Content-Type)
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new RuntimeException(
                "File type not allowed. Allowed types: JPG, PNG, GIF, WebP, BMP"
            );
        }

        // ✅ التحقق من Content-Type مع السماح بـ octet-stream
        String contentType = file.getContentType();
        if (contentType != null
                && !contentType.equals("application/octet-stream")
                && !contentType.startsWith("image/")) {
            throw new RuntimeException(
                "Invalid file type. Only image files are allowed"
            );
        }

        // ✅ إذا كان Content-Type هو octet-stream، نحدده من الامتداد
        if (contentType == null || contentType.equals("application/octet-stream")) {
            contentType = getContentTypeFromExtension(extension);
        }

        // ✅ إنشاء مجلد الرفع
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // ✅ اسم فريد للملف
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;

        // ✅ حفظ الملف
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // ✅ حفظ في قاعدة البيانات
        Image image = new Image();
        image.setFileName(uniqueFileName);
        image.setFilePath(filePath.toString());
        image.setFileType(contentType);
        image.setFileSize(file.getSize());
        image.setUploadDate(today);
        image.setUploadedBy(uploadedBy);
        image.setActive(true);

        return imageRepository.save(image);
    }

    // ✅ استخراج الامتداد من اسم الملف
    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg"; // افتراضي
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    // ✅ تحديد Content-Type من الامتداد
    private String getContentTypeFromExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "bmp":
                return "image/bmp";
            case "heic":
            case "heif":
                return "image/heic";
            default:
                return "image/jpeg";
        }
    }

    public Optional<Image> getTodayImage() {
        return imageRepository.findByUploadDateAndActiveTrue(LocalDate.now());
    }

    public Optional<Image> getLatestImage() {
        return imageRepository.findFirstByActiveTrueOrderByUploadDateDesc();
    }

    public String getImageUrl(String baseUrl, Image image) {
        return baseUrl + "/uploads/" + image.getFileName();
    }
}