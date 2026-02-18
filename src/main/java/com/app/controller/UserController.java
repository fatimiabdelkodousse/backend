package com.app.controller;

import com.app.dto.ApiResponse;
import com.app.dto.FcmTokenRequest;
import com.app.dto.PrayerTimeResponse;
import com.app.entity.Image;
import com.app.service.FcmService;
import com.app.service.ImageService;
import com.app.service.IpLocationService;
import com.app.service.PrayerTimeService;
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

    /**
     * جلب وقت المغرب حسب IP المستخدم
     */
    @GetMapping("/prayer-times")
    public ResponseEntity<ApiResponse<PrayerTimeResponse>> getPrayerTimes(
            HttpServletRequest request) {
        String ipAddress = ipLocationService.extractIpFromRequest(request);
        PrayerTimeResponse response = prayerTimeService.getPrayerTimesByIp(ipAddress);
        return ResponseEntity.ok(
                ApiResponse.success("Prayer times retrieved", response));
    }

    /**
     * جلب صورة اليوم
     */
    @GetMapping("/image/today")
    public ResponseEntity<ApiResponse<Image>> getTodayImage(HttpServletRequest request) {
        return imageService.getTodayImage()
                .map(image -> {
                    String baseUrl = getBaseUrl(request);
                    image.setFilePath(baseUrl + "/uploads/" + image.getFileName());
                    return ResponseEntity.ok(
                            ApiResponse.success("Today's image", image));
                })
                .orElse(ResponseEntity.ok(
                        ApiResponse.<Image>error("No image for today")));
    }

    /**
     * تسجيل FCM Token
     */
    @PostMapping("/fcm/register")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(
            @Valid @RequestBody FcmTokenRequest tokenRequest,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            fcmService.saveOrUpdateToken(
                    userEmail,
                    tokenRequest.getToken(),
                    tokenRequest.getDeviceType()
            );
            return ResponseEntity.ok(ApiResponse.success("FCM token registered"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + 
               request.getServerName() + ":" + 
               request.getServerPort();
    }
}