package com.app.controller;

import com.app.dto.*;
import com.app.entity.FcmToken;
import com.app.entity.Image;
import com.app.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}