package com.app.controller;

import com.app.dto.ApiResponse;
import com.app.dto.PrayerTimeResponse;
import com.app.service.IpLocationService;
import com.app.service.PrayerTimeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prayer/public")
public class PrayerController {

    @Autowired
    private PrayerTimeService prayerTimeService;

    @Autowired
    private IpLocationService ipLocationService;

    /**
     * Endpoint عام لجلب مواقيت الصلاة
     * لا يحتاج تسجيل دخول - مفيد للاختبار
     */
    @GetMapping("/times")
    public ResponseEntity<ApiResponse<PrayerTimeResponse>> getPrayerTimes(
            HttpServletRequest request) {
        String ipAddress = ipLocationService.extractIpFromRequest(request);
        PrayerTimeResponse response = prayerTimeService.getPrayerTimesByIp(ipAddress);
        return ResponseEntity.ok(
                ApiResponse.success("Prayer times", response));
    }

    @GetMapping("/times/{countryCode}")
    public ResponseEntity<ApiResponse<PrayerTimeResponse>> getPrayerTimesByCountry(
            @PathVariable String countryCode,
            @RequestParam(defaultValue = "Unknown") String city) {
        PrayerTimeResponse response = 
                prayerTimeService.getPrayerTimesByCountry(countryCode, countryCode, city);
        return ResponseEntity.ok(
                ApiResponse.success("Prayer times", response));
    }
}