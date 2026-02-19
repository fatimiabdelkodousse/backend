package com.app.service;

import com.app.dto.MessageResponse;
import com.app.dto.NotificationSettingsRequest;
import com.app.dto.SendMessageRequest;
import com.app.entity.*;
import com.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageRecipientRepository recipientRepository;

    @Autowired
    private NotificationSettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FcmService fcmService;

    // ==================== إرسال رسالة ====================
    @Transactional
    public void sendMessage(String adminEmail,
                            SendMessageRequest request) {

        // إنشاء الرسالة
        Message message = new Message();
        message.setContent(request.getContent());
        message.setSentBy(adminEmail);

        List<User> targetUsers = new ArrayList<>();

        if ("ALL".equals(request.getTargetType())) {
            message.setTargetType(MessageTargetType.ALL);
            // جلب كل المستخدمين النشطين
            targetUsers = userRepository.findByRoleAndEnabledTrue(
                    Role.ROLE_USER);
        } else {
            message.setTargetType(MessageTargetType.SPECIFIC);
            // جلب المستخدمين المحددين
            if (request.getUserIds() != null) {
                for (Long userId : request.getUserIds()) {
                    userRepository.findById(userId)
                            .ifPresent(targetUsers::add);
                }
            }
        }

        messageRepository.save(message);

        // إنشاء سجل لكل مستلم
        List<String> fcmTokens = new ArrayList<>();

        for (User user : targetUsers) {
            // التحقق من إعدادات الإشعارات
            boolean canReceive = canUserReceiveMessages(user);

            MessageRecipient recipient = new MessageRecipient();
            recipient.setMessage(message);
            recipient.setUser(user);
            recipient.setRead(false);
            recipientRepository.save(recipient);

            // جمع الـ FCM Tokens للمستخدمين الذين يقبلون الإشعارات
            if (canReceive) {
                user.getFcmTokens().forEach(t -> {
                    if (t.isActive()) fcmTokens.add(t.getToken());
                });
            }
        }

        // إرسال Push Notification
        if (!fcmTokens.isEmpty()) {
            fcmService.sendMulticastNotification(
                    fcmTokens,
                    "📬 تم تعبئة الكرش بنجاح",
                    request.getContent(),
                    "message"
            );
        }
    }

    // ==================== جلب رسائل المستخدم ====================
    public List<MessageResponse> getUserMessages(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MessageRecipient> recipients =
                recipientRepository.findByUserOrderByCreatedAtDesc(user);

        return recipients.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==================== عدد الرسائل الغير مقروءة ====================
    public long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return recipientRepository.countByUserAndReadFalse(user);
    }

    // ==================== تحديد رسالة كمقروءة ====================
    @Transactional
    public void markAsRead(Long recipientId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        recipientRepository.findByIdAndUser(recipientId, user)
                .ifPresent(r -> {
                    r.setRead(true);
                    r.setReadAt(LocalDateTime.now());
                    recipientRepository.save(r);
                });
    }

    // ==================== تحديد الكل كمقروء ====================
    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        recipientRepository.markAllAsReadForUser(user);
    }

    // ==================== إعدادات الإشعارات ====================
    public NotificationSettings getOrCreateSettings(User user) {
        return settingsRepository.findByUser(user)
                .orElseGet(() -> {
                    NotificationSettings settings =
                            new NotificationSettings();
                    settings.setUser(user);
                    settings.setNotificationsEnabled(true);
                    settings.setMessagesEnabled(true);
                    settings.setMaghrebEnabled(true);
                    return settingsRepository.save(settings);
                });
    }

    public NotificationSettings updateSettings(
            String userEmail, NotificationSettingsRequest request) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationSettings settings = getOrCreateSettings(user);
        settings.setNotificationsEnabled(request.isNotificationsEnabled());
        settings.setMessagesEnabled(request.isMessagesEnabled());
        settings.setMaghrebEnabled(request.isMaghrebEnabled());

        return settingsRepository.save(settings);
    }

    public NotificationSettings getSettings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getOrCreateSettings(user);
    }

    // ==================== Helper ====================
    private boolean canUserReceiveMessages(User user) {
        return settingsRepository.findByUser(user)
                .map(s -> s.isNotificationsEnabled() && s.isMessagesEnabled())
                .orElse(true);
    }

    private MessageResponse toResponse(MessageRecipient recipient) {
        MessageResponse response = new MessageResponse();
        response.setId(recipient.getMessage().getId());
        response.setRecipientId(recipient.getId());
        response.setContent(recipient.getMessage().getContent());
        response.setSentBy(recipient.getMessage().getSentBy());
        response.setRead(recipient.isRead());
        response.setReadAt(recipient.getReadAt());
        response.setCreatedAt(recipient.getMessage().getCreatedAt());
        return response;
    }
}