package com.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class IpLocationService {

    @Value("${ipgeo.api.key}")
    private String apiKey;

    @Value("${ipgeo.api.url}")
    private String apiUrl;

    private final WebClient webClient;

    public IpLocationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * يستخرج معلومات الموقع من الـ IP Address
     * يستخدم API: https://api.ipgeolocation.io
     */
    public Map<String, String> getLocationFromIp(String ipAddress) {
        Map<String, String> locationData = new HashMap<>();

        try {
            // في حالة localhost أو IP خاص
            if (isLocalIp(ipAddress)) {
                locationData.put("countryCode", "SA");
                locationData.put("countryName", "Saudi Arabia");
                locationData.put("city", "Riyadh");
                return locationData;
            }

            // استدعاء الـ API
            Map response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.ipgeolocation.io")
                            .path("/ipgeo")
                            .queryParam("apiKey", apiKey)
                            .queryParam("ip", ipAddress)
                            .queryParam("fields", "country_code2,country_name,city")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                locationData.put("countryCode", 
                        (String) response.getOrDefault("country_code2", "SA"));
                locationData.put("countryName", 
                        (String) response.getOrDefault("country_name", "Saudi Arabia"));
                locationData.put("city", 
                        (String) response.getOrDefault("city", "Riyadh"));
            }
        } catch (Exception e) {
            // Fallback في حالة فشل الـ API
            System.err.println("IP Location API error: " + e.getMessage());
            locationData.put("countryCode", "SA");
            locationData.put("countryName", "Saudi Arabia");
            locationData.put("city", "Riyadh");
        }

        return locationData;
    }

    /**
     * يستخرج الـ IP الحقيقي من الـ Request
     * يأخذ بعين الاعتبار الـ Proxy و Load Balancer
     */
    public String extractIpFromRequest(jakarta.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // إذا كان هناك عدة IPs (عند استخدام Proxy)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    private boolean isLocalIp(String ip) {
        return ip == null 
                || ip.equals("127.0.0.1") 
                || ip.equals("::1")
                || ip.startsWith("192.168.")
                || ip.startsWith("10.")
                || ip.startsWith("172.");
    }
}