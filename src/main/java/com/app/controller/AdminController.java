package com.app.controller;

import com.app.dto.ApiResponse;
import com.app.dto.CreateUserRequest;
import com.app.entity.Image;
import com.app.entity.User;
import com.app.service.AdminService;
import com.app.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ImageService imageService;

    /**
     * إنشاء مستخدم جديد
     */
    @PostMapping("/users/create")
    public ResponseEntity<ApiResponse<User>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        try {
            User user = adminService.createUser(request);
            // نخفي الـ password في الرد
            user.setPassword("");
            return ResponseEntity.ok(
                    ApiResponse.success("User created successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * جلب قائمة المستخدمين
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        users.forEach(u -> u.setPassword(""));
        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved", users));
    }

    /**
     * حذف مستخدم
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * تفعيل/تعطيل مستخدم
     */
    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<User>> toggleUser(@PathVariable Long id) {
        try {
            User user = adminService.toggleUserStatus(id);
            user.setPassword("");
            return ResponseEntity.ok(
                    ApiResponse.success("User status updated", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * رفع صورة
     */
    @PostMapping("/image/upload")
    public ResponseEntity<ApiResponse<Image>> uploadImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            HttpServletRequest request) {
        try {
            String uploadedBy = authentication.getName();
            Image image = imageService.uploadImage(file, uploadedBy);

            // بناء URL الصورة
            String baseUrl = request.getScheme() + "://" + 
                    request.getServerName() + ":" + request.getServerPort();
            image.setFilePath(baseUrl + "/uploads/" + image.getFileName());

            return ResponseEntity.ok(
                    ApiResponse.success("Image uploaded successfully", image));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * جلب صورة اليوم
     */
    @GetMapping("/image/today")
    public ResponseEntity<ApiResponse<Image>> getTodayImage() {
        return imageService.getTodayImage()
                .map(image -> ResponseEntity.ok(
                        ApiResponse.success("Today's image", image)))
                .orElse(ResponseEntity.ok(
                        ApiResponse.error("No image uploaded today")));
    }
}