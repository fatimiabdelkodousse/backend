package com.app.controller;

import com.app.dto.*;
import com.app.entity.Image;
import com.app.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.app.entity.NotificationSettings;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class UserController {

    @Autowired
    private PrayerTimeService prayerTimeService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FcmService fcmService;

    @Autowired
    private IpLocationService ipLocationService;

    @Autowired
    private UserService userService;

    @Autowired
    private KarshService karshService;

    // ✅ جلب مواقيت الصلاة
    @GetMapping("/prayer-times")
    public ResponseEntity<ApiResponse<PrayerTimeResponse>> getPrayerTimes(
            HttpServletRequest request) {
        String ip = ipLocationService.extractIpFromRequest(request);
        PrayerTimeResponse response = prayerTimeService.getPrayerTimesByIp(ip);
        return ResponseEntity.ok(ApiResponse.success("Prayer times", response));
    }

    // ✅ جلب صورة اليوم
    @GetMapping("/image/today")
    public ResponseEntity<ApiResponse<Image>> getTodayImage(
            HttpServletRequest request) {
        return imageService.getTodayImage()
                .map(image -> {
                    String base = getBaseUrl(request);
                    image.setFilePath(base + "/uploads/" + image.getFileName());
                    return ResponseEntity.ok(
                            ApiResponse.success("Today's image", image));
                })
                .orElse(ResponseEntity.ok(
                        ApiResponse.<Image>error("No image for today")));
    }

    // ✅ تسجيل FCM Token
    @PostMapping("/fcm/register")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(
            @Valid @RequestBody FcmTokenRequest req,
            Authentication auth) {
        fcmService.saveOrUpdateToken(
                auth.getName(), req.getToken(), req.getDeviceType());
        return ResponseEntity.ok(ApiResponse.success("FCM token registered"));
    }

    // ✅ جلب ملف المستخدم مع النقاط
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            Authentication auth) {
        UserProfileResponse profile = userService.getUserProfile(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile", profile));
    }

    // ✅ إرسال إجابة سؤال الكرش
    @PostMapping("/karsh/answer")
    public ResponseEntity<ApiResponse<KarshAnswerResponse>> submitKarshAnswer(
            @Valid @RequestBody KarshAnswerRequest request,
            Authentication auth) {
        try {
            KarshAnswerResponse response =
                    karshService.submitAnswer(auth.getName(), request);
            return ResponseEntity.ok(
                    ApiResponse.success("Answer submitted", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort();
    }
    
 // أضف هذا في UserController.java

    @Autowired
    private MessageService messageService;

    // ==================== جلب الرسائل ====================
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            Authentication auth) {
        List<MessageResponse> messages =
                messageService.getUserMessages(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Messages", messages));
    }

    // ==================== عدد الغير مقروء ====================
    @GetMapping("/messages/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            Authentication auth) {
        long count = messageService.getUnreadCount(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Unread count", count));
    }

    // ==================== تحديد كمقروء ====================
    @PutMapping("/messages/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id, Authentication auth) {
        messageService.markAsRead(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Marked as read"));
    }

    // ==================== تحديد الكل كمقروء ====================
    @PutMapping("/messages/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            Authentication auth) {
        messageService.markAllAsRead(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("All marked as read"));
    }

    // ==================== جلب إعدادات الإشعارات ====================
    @GetMapping("/notifications/settings")
    public ResponseEntity<ApiResponse<NotificationSettings>> getSettings(
            Authentication auth) {
        NotificationSettings settings =
                messageService.getSettings(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Settings", settings));
    }

    // ==================== تحديث إعدادات الإشعارات ====================
    @PutMapping("/notifications/settings")
    public ResponseEntity<ApiResponse<NotificationSettings>> updateSettings(
            @RequestBody NotificationSettingsRequest request,
            Authentication auth) {
        NotificationSettings settings =
                messageService.updateSettings(auth.getName(), request);
        return ResponseEntity.ok(
                ApiResponse.success("Settings updated", settings));
    }
}