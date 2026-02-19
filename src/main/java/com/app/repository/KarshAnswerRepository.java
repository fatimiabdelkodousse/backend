package com.app.repository;

import com.app.entity.KarshAnswer;
import com.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KarshAnswerRepository extends JpaRepository<KarshAnswer, Long> {

    boolean existsByUserAndAnswerDate(User user, LocalDate date);

    Optional<KarshAnswer> findByUserAndAnswerDate(User user, LocalDate date);

    // ✅ أضف هذا لحذف إجابات المستخدم عند الحذف
    List<KarshAnswer> findByUser(User user);
}