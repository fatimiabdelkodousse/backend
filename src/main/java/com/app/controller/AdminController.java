package com.app.controller;

import com.app.dto.*;
import com.app.service.AdminService;
import com.app.service.ImageService;
import com.app.entity.Image;
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

    // ==================== Create User ====================
    @PostMapping("/users/create")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        try {
            UserDTO user = adminService.createUser(request);
            return ResponseEntity.ok(
                ApiResponse.success("تم إنشاء المستخدم بنجاح", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== Get All Users ====================
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        try {
            List<UserDTO> users = adminService.getAllUsers();
            return ResponseEntity.ok(
                ApiResponse.success("تم جلب المستخدمين", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== Delete User ====================
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(
                ApiResponse.success("تم حذف المستخدم بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== Toggle User ====================
    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<UserDTO>> toggleUser(
            @PathVariable Long id) {
        try {
            UserDTO user = adminService.toggleUserStatus(id);
            return ResponseEntity.ok(
                ApiResponse.success("تم تحديث الحالة", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== Upload Image ====================
    @PostMapping("/image/upload")
    public ResponseEntity<ApiResponse<Image>> uploadImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            HttpServletRequest request) {
        try {
            String uploadedBy = authentication.getName();
            Image image = imageService.uploadImage(file, uploadedBy);

            String baseUrl = request.getScheme() + "://"
                    + request.getServerName() + ":"
                    + request.getServerPort();
            image.setFilePath(baseUrl + "/uploads/" + image.getFileName());

            return ResponseEntity.ok(
                ApiResponse.success("تم رفع الصورة بنجاح", image));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== Today Image ====================
    @GetMapping("/image/today")
    public ResponseEntity<ApiResponse<Image>> getTodayImage() {
        return imageService.getTodayImage()
            .map(image -> ResponseEntity.ok(
                ApiResponse.success("صورة اليوم", image)))
            .orElse(ResponseEntity.ok(
                ApiResponse.<Image>error("لا توجد صورة اليوم")));
    }

    // ==================== Send Message ====================
    @PostMapping("/messages/send")
    public ResponseEntity<ApiResponse<Void>> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication) {
        try {
            // messageService.sendMessage(authentication.getName(), request);
            return ResponseEntity.ok(
                ApiResponse.success("تم إرسال الرسالة بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}