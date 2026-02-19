package com.app.repository;

import com.app.entity.MessageRecipient;
import com.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRecipientRepository
        extends JpaRepository<MessageRecipient, Long> {

    // جلب رسائل مستخدم مرتبة من الأحدث
    List<MessageRecipient> findByUserOrderByCreatedAtDesc(User user);

    // عدد الرسائل الغير مقروءة
    long countByUserAndReadFalse(User user);

    // تحديد رسالة كمقروءة
    Optional<MessageRecipient> findByIdAndUser(Long id, User user);

    // تحديد جميع رسائل المستخدم كمقروءة
    @Modifying
    @Transactional
    @Query("UPDATE MessageRecipient m SET m.read = true, " +
           "m.readAt = CURRENT_TIMESTAMP WHERE m.user = :user")
    void markAllAsReadForUser(User user);
}