package com.app.service;

import com.app.dto.CreateUserRequest;
import com.app.entity.Role;
import com.app.entity.User;
import com.app.repository.FcmTokenRepository;
import com.app.repository.KarshAnswerRepository;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private KarshAnswerRepository karshAnswerRepository;

    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                "البريد الإلكتروني مستخدم مسبقاً: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setPoints(0); // ← النقاط تبدأ من صفر

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findByRole(Role.ROLE_USER);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new RuntimeException("لا يمكن حذف حساب الأدمن");
        }

        // ✅ حذف FCM Tokens أولاً
        List<com.app.entity.FcmToken> tokens =
                fcmTokenRepository.findByUserAndActiveTrue(user);
        fcmTokenRepository.deleteAll(tokens);

        // ✅ حذف إجابات الكرش
        List<com.app.entity.KarshAnswer> answers =
                karshAnswerRepository.findByUser(user);
        karshAnswerRepository.deleteAll(answers);

        // ✅ حذف المستخدم
        userRepository.delete(user);
    }

    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }
}