package com.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class IpLocationService {

    @Value("${ipgeo.api.key:}")
    private String apiKey;

    @Value("${ipgeo.api.url:https://api.ipgeolocation.io/ipgeo}")
    private String apiUrl;

    private final WebClient webClient;

    public IpLocationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Map<String, String> getLocationFromIp(String ipAddress) {

        Map<String, String> locationData = new HashMap<>();

        try {

            if (isLocalIp(ipAddress)) {
                return getDefaultLocation();
            }

            Map response;

            // إذا يوجد API Key → استخدم ipgeolocation.io
            if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-ipgeo-api-key")) {

                response = webClient.get()
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

            } else {
                // fallback إلى ip-api.com المجاني

                response = webClient.get()
                        .uri("http://ip-api.com/json/" + ipAddress +
                                "?fields=status,country,countryCode,city")
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response != null && "success".equals(response.get("status"))) {
                    locationData.put("countryCode",
                            (String) response.getOrDefault("countryCode", "SA"));
                    locationData.put("countryName",
                            (String) response.getOrDefault("country", "Saudi Arabia"));
                    locationData.put("city",
                            (String) response.getOrDefault("city", "Riyadh"));
                }
            }

        } catch (Exception e) {
            System.err.println("IP Location API error: " + e.getMessage());
            return getDefaultLocation();
        }

        return locationData.isEmpty() ? getDefaultLocation() : locationData;
    }

    public String extractIpFromRequest(jakarta.servlet.http.HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();

        if (ip != null && ip.contains(","))
            ip = ip.split(",")[0].trim();

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

    private Map<String, String> getDefaultLocation() {
        Map<String, String> defaultLocation = new HashMap<>();
        defaultLocation.put("countryCode", "SA");
        defaultLocation.put("countryName", "Saudi Arabia");
        defaultLocation.put("city", "Riyadh");
        return defaultLocation;
    }
}
