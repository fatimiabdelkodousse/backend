package com.app.service;

import com.app.dto.KarshAnswerRequest;
import com.app.dto.KarshAnswerResponse;
import com.app.entity.KarshAnswer;
import com.app.entity.User;
import com.app.repository.KarshAnswerRepository;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class KarshService {

    @Autowired
    private KarshAnswerRepository karshAnswerRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public KarshAnswerResponse submitAnswer(
            String userEmail, KarshAnswerRequest request) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();

        // ✅ التحقق إذا المستخدم أجاب اليوم
        if (karshAnswerRepository.existsByUserAndAnswerDate(user, today)) {
            KarshAnswer existing =
                    karshAnswerRepository
                            .findByUserAndAnswerDate(user, today)
                            .orElseThrow();

            KarshAnswerResponse response = new KarshAnswerResponse();
            response.setAlreadyAnswered(true);
            response.setAnsweredYes(existing.isAnsweredYes());
            response.setTotalPoints(user.getPoints());
            response.setMessage("لقد أجبت على هذا السؤال اليوم مسبقاً");
            return response;
        }

        boolean answeredYes = request.getAnsweredYes();
        int pointsChanged   = answeredYes ? +1 : -1;

        // ✅ تحديث النقاط
        int newPoints = user.getPoints() + pointsChanged;
        // النقاط لا تقل عن صفر
        if (newPoints < 0) newPoints = 0;
        user.setPoints(newPoints);
        userRepository.save(user);

        // ✅ حفظ الإجابة
        KarshAnswer answer = new KarshAnswer();
        answer.setUser(user);
        answer.setAnswerDate(today);
        answer.setAnsweredYes(answeredYes);
        answer.setPointsChanged(pointsChanged);
        karshAnswerRepository.save(answer);

        // ✅ بناء الرد
        KarshAnswerResponse response = new KarshAnswerResponse();
        response.setAnsweredYes(answeredYes);
        response.setPointsChanged(pointsChanged);
        response.setTotalPoints(newPoints);
        response.setAlreadyAnswered(false);

        if (answeredYes) {
            response.setMessage(
                "كفو عليك يا بطل 😍🎉 بس متخليش الكرش تلهيك عن التراويح");
        } else {
            response.setMessage(
                "عشان الجواب دا راح يتم خصم نقطة من نقاط " +
                "\"تم تعبئة الكرش بنجاح\" من حسابك");
        }

        return response;
    }

    public boolean hasAnsweredToday(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return karshAnswerRepository.existsByUserAndAnswerDate(
                user, LocalDate.now());
    }

    public KarshAnswer getTodayAnswer(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return karshAnswerRepository
                .findByUserAndAnswerDate(user, LocalDate.now())
                .orElse(null);
    }
}