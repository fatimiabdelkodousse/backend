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
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public Image uploadImage(MultipartFile file, String uploadedBy) throws IOException {
        LocalDate today = LocalDate.now();

        // التحقق من أن الأدمن لم يرفع صورة اليوم
        if (imageRepository.existsByUploadDate(today)) {
            throw new RuntimeException("An image has already been uploaded today");
        }

        // التحقق من نوع الملف
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        // إنشاء مجلد الرفع إذا لم يكن موجوداً
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // إنشاء اسم فريد للملف
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // حفظ الملف
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // حفظ معلومات الصورة في قاعدة البيانات
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