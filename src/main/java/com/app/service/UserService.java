package com.app.service;

import com.app.dto.UserProfileResponse;
import com.app.entity.KarshAnswer;
import com.app.entity.User;
import com.app.repository.KarshAnswerRepository;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KarshAnswerRepository karshAnswerRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserProfileResponse getUserProfile(String email) {
        User user = getUserByEmail(email);

        boolean answeredToday = karshAnswerRepository
                .existsByUserAndAnswerDate(user, LocalDate.now());

        Boolean todayAnswer = null;
        if (answeredToday) {
            KarshAnswer answer = karshAnswerRepository
                    .findByUserAndAnswerDate(user, LocalDate.now())
                    .orElse(null);
            if (answer != null) {
                todayAnswer = answer.isAnsweredYes();
            }
        }

        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(user.getId());
        profile.setEmail(user.getEmail());
        profile.setPoints(user.getPoints());
        profile.setAnsweredToday(answeredToday);
        profile.setTodayAnswer(todayAnswer);

        return profile;
    }
}