package com.app.scheduler;

import com.app.entity.PrayerTime;
import com.app.repository.PrayerTimeRepository;
import com.app.service.FcmService;
import com.app.service.PrayerTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@EnableScheduling
public class MaghrebScheduler {

    @Autowired
    private PrayerTimeRepository prayerTimeRepository;

    @Autowired
    private PrayerTimeService prayerTimeService;

    @Autowired
    private FcmService fcmService;

    /**
     * يتحقق كل دقيقة من وقت المغرب
     * ويرسل الإشعار عند الوصول للوقت
     */
    @Scheduled(fixedRate = 60000) // كل 60 ثانية
    public void checkMaghrebAndNotify() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime nowWithoutSeconds = now.withSecond(0).withNano(0);

        // جلب جميع مواقيت اليوم التي لم يُرسل لها إشعار
        List<PrayerTime> prayerTimes =
                prayerTimeRepository.findByPrayerDateAndNotificationSentFalse(today);

        for (PrayerTime pt : prayerTimes) {
            LocalTime maghrebTime = pt.getMaghrebTime().withSecond(0).withNano(0);
            
            // التحقق إذا وصلنا لوقت المغرب (بدقيقة تقريبية)
            if (!nowWithoutSeconds.isBefore(maghrebTime) && 
                nowWithoutSeconds.isBefore(maghrebTime.plusMinutes(2))) {
                
                System.out.println("🌅 Maghreb time reached for: " + pt.getCountryCode());
                
                // إرسال الإشعار
                fcmService.sendMaghrebNotificationToAll();
                
                // تحديث حالة الإرسال
                pt.setNotificationSent(true);
                prayerTimeRepository.save(pt);
                
                System.out.println("✅ Notification sent for: " + pt.getCountryCode());
            }
        }
    }

    /**
     * كل يوم الساعة 3 فجراً - تجديد مواقيت الصلاة لكل الدول
     * يتأكد أن البيانات محدثة
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshDailyPrayerTimes() {
        System.out.println("🔄 Refreshing daily prayer times...");
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // قائمة الدول الافتراضية لتحديثها مسبقاً
        String[][] countries = {
            {"SA", "Saudi Arabia", "Riyadh"},
            {"AE", "United Arab Emirates", "Dubai"},
            {"EG", "Egypt", "Cairo"},
            {"KW", "Kuwait", "Kuwait City"},
            {"QA", "Qatar", "Doha"},
            {"BH", "Bahrain", "Manama"},
            {"OM", "Oman", "Muscat"},
            {"MA", "Morocco", "Casablanca"},
            {"TN", "Tunisia", "Tunis"},
            {"DZ", "Algeria", "Algiers"}
        };

        for (String[] country : countries) {
            try {
                if (!prayerTimeRepository.existsByCountryCodeAndPrayerDate(
                        country[0], tomorrow)) {
                    prayerTimeService.fetchAndSavePrayerTimes(
                            country[0], country[1], country[2], tomorrow);
                    System.out.println("✅ Updated prayer times for: " + country[0]);
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to update for " + country[0] + ": " 
                        + e.getMessage());
            }
        }
    }
}