package com.app.service;

import com.app.entity.FcmToken;
import com.app.entity.User;
import com.app.repository.FcmTokenRepository;
import com.app.repository.UserRepository;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FcmService {

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * حفظ أو تحديث FCM Token للمستخدم
     */
    public FcmToken saveOrUpdateToken(String userEmail, String token, String deviceType) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<FcmToken> existingToken = fcmTokenRepository.findByToken(token);

        if (existingToken.isPresent()) {
            FcmToken fcmToken = existingToken.get();
            fcmToken.setUser(user);
            fcmToken.setActive(true);
            return fcmTokenRepository.save(fcmToken);
        }

        FcmToken newToken = new FcmToken();
        newToken.setToken(token);
        newToken.setUser(user);
        newToken.setDeviceType(deviceType);
        newToken.setActive(true);

        return fcmTokenRepository.save(newToken);
    }

    /**
     * إرسال إشعار لجميع المستخدمين النشطين
     */
    public void sendMaghrebNotificationToAll() {
        List<FcmToken> activeTokens = fcmTokenRepository.findByActiveTrue();

        if (activeTokens.isEmpty()) {
            System.out.println("No active FCM tokens found");
            return;
        }

        List<String> tokenList = new ArrayList<>();
        for (FcmToken fcmToken : activeTokens) {
            tokenList.add(fcmToken.getToken());
        }

        sendMulticastNotification(
                tokenList,
                "🌅 وقت الإفطار",
                "تم تعبئة الكرش بنجاح 🎉",
                "maghreb"
        );
    }

    /**
     * إرسال إشعار لمستخدم واحد
     */
    public void sendNotificationToUser(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("type", "maghreb")
                    .putData("timestamp", String.valueOf(System.currentTimeMillis()))
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .setBadge(1)
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification sent successfully: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending notification: " + e.getMessage());
            // إذا كان الـ Token غير صالح - احذفه
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                fcmTokenRepository.findByToken(token).ifPresent(t -> {
                    t.setActive(false);
                    fcmTokenRepository.save(t);
                });
            }
        }
    }

    /**
     * إرسال إشعار لمجموعة من المستخدمين
     */
    public void sendMulticastNotification(List<String> tokens, 
                                          String title, String body, String type) {
        if (tokens.isEmpty()) return;

        // Firebase يسمح بـ 500 token كحد أقصى في طلب واحد
        int batchSize = 500;
        for (int i = 0; i < tokens.size(); i += batchSize) {
            List<String> batch = tokens.subList(i, Math.min(i + batchSize, tokens.size()));
            sendBatch(batch, title, body, type);
        }
    }

    private void sendBatch(List<String> tokens, String title, String body, String type) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("type", type)
                    .putData("timestamp", String.valueOf(System.currentTimeMillis()))
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setChannelId("maghreb_channel")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .setBadge(1)
                                    .build())
                            .build())
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(message);
            
            System.out.println("Successfully sent: " + response.getSuccessCount());
            System.out.println("Failed: " + response.getFailureCount());

            // معالجة الـ Tokens الفاشلة
            List<SendResponse> responses = response.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    String failedToken = tokens.get(i);
                    fcmTokenRepository.findByToken(failedToken).ifPresent(t -> {
                        t.setActive(false);
                        fcmTokenRepository.save(t);
                    });
                }
            }
        } catch (FirebaseMessagingException e) {
            System.err.println("Batch notification error: " + e.getMessage());
        }
    }
}