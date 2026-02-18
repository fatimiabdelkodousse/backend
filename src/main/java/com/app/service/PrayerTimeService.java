package com.app.service;

import com.app.dto.PrayerTimeResponse;
import com.app.entity.PrayerTime;
import com.app.repository.PrayerTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@Service
public class PrayerTimeService {

    @Autowired
    private PrayerTimeRepository prayerTimeRepository;

    @Autowired
    private IpLocationService ipLocationService;

    private final WebClient webClient;

    @Value("${prayer.api.url}")
    private String prayerApiUrl;

    // Mapping بين رمز الدولة والـ Timezone
    private static final Map<String, String> COUNTRY_TIMEZONE_MAP = Map.ofEntries(
            Map.entry("SA", "Asia/Riyadh"),
            Map.entry("AE", "Asia/Dubai"),
            Map.entry("EG", "Africa/Cairo"),
            Map.entry("KW", "Asia/Kuwait"),
            Map.entry("BH", "Asia/Bahrain"),
            Map.entry("QA", "Asia/Qatar"),
            Map.entry("OM", "Asia/Muscat"),
            Map.entry("JO", "Asia/Amman"),
            Map.entry("LB", "Asia/Beirut"),
            Map.entry("IQ", "Asia/Baghdad"),
            Map.entry("SY", "Asia/Damascus"),
            Map.entry("YE", "Asia/Aden"),
            Map.entry("MA", "Africa/Casablanca"),
            Map.entry("TN", "Africa/Tunis"),
            Map.entry("DZ", "Africa/Algiers"),
            Map.entry("LY", "Africa/Tripoli"),
            Map.entry("SD", "Africa/Khartoum"),
            Map.entry("PK", "Asia/Karachi"),
            Map.entry("TR", "Europe/Istanbul"),
            Map.entry("US", "America/New_York"),
            Map.entry("GB", "Europe/London"),
            Map.entry("DE", "Europe/Berlin"),
            Map.entry("FR", "Europe/Paris")
    );

    public PrayerTimeService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * جلب مواقيت الصلاة حسب الـ IP Address
     */
    public PrayerTimeResponse getPrayerTimesByIp(String ipAddress) {
        // استخراج الدولة من الـ IP
        Map<String, String> locationData = ipLocationService.getLocationFromIp(ipAddress);
        String countryCode = locationData.get("countryCode");
        String countryName = locationData.get("countryName");
        String city = locationData.get("city");

        return getPrayerTimesByCountry(countryCode, countryName, city);
    }

    /**
     * جلب مواقيت الصلاة حسب رمز الدولة
     */
    public PrayerTimeResponse getPrayerTimesByCountry(
            String countryCode, String countryName, String city) {
        
        LocalDate today = LocalDate.now();

        // البحث في قاعدة البيانات أولاً
        Optional<PrayerTime> existingPrayerTime =
                prayerTimeRepository.findByCountryCodeAndPrayerDate(countryCode, today);

        PrayerTime prayerTime;
        if (existingPrayerTime.isPresent()) {
            prayerTime = existingPrayerTime.get();
        } else {
            // جلب من الـ API وحفظ في قاعدة البيانات
            prayerTime = fetchAndSavePrayerTimes(countryCode, countryName, city, today);
        }

        return buildResponse(prayerTime, countryCode);
    }

    /**
     * جلب مواقيت الصلاة من API الخارجية وحفظها
     * تستخدم: https://api.aladhan.com/v1/timingsByCity
     */
    public PrayerTime fetchAndSavePrayerTimes(
            String countryCode, String countryName, String city, LocalDate date) {
        try {
            String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            Map response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.aladhan.com")
                            .path("/v1/timingsByCity/{date}")
                            .queryParam("city", city)
                            .queryParam("country", countryCode)
                            .queryParam("method", 4) // Method 4 = Umm Al-Qura
                            .build(dateStr))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                Map data = (Map) response.get("data");
                Map timings = (Map) data.get("timings");

                PrayerTime prayerTime = new PrayerTime();
                prayerTime.setCountryCode(countryCode);
                prayerTime.setCountryName(countryName);
                prayerTime.setCity(city);
                prayerTime.setPrayerDate(date);
                prayerTime.setFajrTime(parseTime((String) timings.get("Fajr")));
                prayerTime.setDhuhrTime(parseTime((String) timings.get("Dhuhr")));
                prayerTime.setAsrTime(parseTime((String) timings.get("Asr")));
                prayerTime.setMaghrebTime(parseTime((String) timings.get("Maghrib")));
                prayerTime.setIshaTime(parseTime((String) timings.get("Isha")));
                prayerTime.setNotificationSent(false);

                return prayerTimeRepository.save(prayerTime);
            }
        } catch (Exception e) {
            System.err.println("Prayer API error for " + countryCode + ": " + e.getMessage());
        }

        // Fallback - وقت افتراضي
        return createDefaultPrayerTime(countryCode, countryName, city, date);
    }

    private PrayerTime createDefaultPrayerTime(
            String countryCode, String countryName, String city, LocalDate date) {
        PrayerTime prayerTime = new PrayerTime();
        prayerTime.setCountryCode(countryCode);
        prayerTime.setCountryName(countryName);
        prayerTime.setCity(city);
        prayerTime.setPrayerDate(date);
        prayerTime.setFajrTime(LocalTime.of(5, 0));
        prayerTime.setDhuhrTime(LocalTime.of(12, 15));
        prayerTime.setAsrTime(LocalTime.of(15, 30));
        prayerTime.setMaghrebTime(LocalTime.of(18, 30));
        prayerTime.setIshaTime(LocalTime.of(20, 0));
        prayerTime.setNotificationSent(false);
        return prayerTimeRepository.save(prayerTime);
    }

    private PrayerTimeResponse buildResponse(PrayerTime prayerTime, String countryCode) {
        PrayerTimeResponse response = new PrayerTimeResponse();
        response.setCountryCode(prayerTime.getCountryCode());
        response.setCountryName(prayerTime.getCountryName());
        response.setCity(prayerTime.getCity());
        response.setPrayerDate(prayerTime.getPrayerDate());
        response.setMaghrebTime(prayerTime.getMaghrebTime().toString());
        response.setFajrTime(prayerTime.getFajrTime() != null ? 
                prayerTime.getFajrTime().toString() : "");
        response.setDhuhrTime(prayerTime.getDhuhrTime() != null ? 
                prayerTime.getDhuhrTime().toString() : "");
        response.setAsrTime(prayerTime.getAsrTime() != null ? 
                prayerTime.getAsrTime().toString() : "");
        response.setIshaTime(prayerTime.getIshaTime() != null ? 
                prayerTime.getIshaTime().toString() : "");

        // حساب الثواني المتبقية على المغرب
        long secondsUntilMaghreb = calculateSecondsUntilMaghreb(
                prayerTime.getMaghrebTime(), countryCode);
        response.setSecondsUntilMaghreb(secondsUntilMaghreb);

        return response;
    }

    /**
     * حساب الثواني المتبقية على وقت المغرب
     * يأخذ بعين الاعتبار الـ Timezone الخاصة بالدولة
     */
    public long calculateSecondsUntilMaghreb(LocalTime maghrebTime, String countryCode) {
        String timezone = COUNTRY_TIMEZONE_MAP.getOrDefault(countryCode, "Asia/Riyadh");
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        
        ZonedDateTime maghrebDateTime = now.toLocalDate()
                .atTime(maghrebTime)
                .atZone(zoneId);

        long seconds = ChronoUnit.SECONDS.between(now, maghrebDateTime);
        
        // إذا كان الوقت قد مضى اليوم
        if (seconds < 0) {
            seconds = 0;
        }
        
        return seconds;
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return LocalTime.of(0, 0);
        // الـ API يُرجع مثل "18:30 (UTC)" أو "18:30"
        String cleanTime = timeStr.split(" ")[0];
        return LocalTime.parse(cleanTime, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public Optional<PrayerTime> findPrayerTimeByCountryAndDate(
            String countryCode, LocalDate date) {
        return prayerTimeRepository.findByCountryCodeAndPrayerDate(countryCode, date);
    }
}