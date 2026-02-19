package com.app.service;

import com.app.dto.CreateUserRequest;
import com.app.dto.UserDTO;
import com.app.entity.FcmToken;
import com.app.entity.KarshAnswer;
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
import java.util.stream.Collectors;

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

    // ==================== Create User ====================
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                "البريد الإلكتروني مستخدم مسبقاً");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setPoints(0);

        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    // ==================== Get All Users ====================
    public List<UserDTO> getAllUsers() {
        return userRepository.findByRole(Role.ROLE_USER)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== Delete User ====================
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new RuntimeException("المستخدم غير موجود"));

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new RuntimeException("لا يمكن حذف حساب الأدمن");
        }

        // حذف FCM Tokens
        List<FcmToken> tokens =
                fcmTokenRepository.findByUserAndActiveTrue(user);
        fcmTokenRepository.deleteAll(tokens);

        // حذف إجابات الكرش
        List<KarshAnswer> answers =
                karshAnswerRepository.findByUser(user);
        karshAnswerRepository.deleteAll(answers);

        // حذف المستخدم
        userRepository.delete(user);
    }

    // ==================== Toggle Status ====================
    public UserDTO toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new RuntimeException("المستخدم غير موجود"));
        user.setEnabled(!user.isEnabled());
        return toDTO(userRepository.save(user));
    }

    // ==================== Convert to DTO ====================
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setEnabled(user.isEnabled());
        dto.setPoints(user.getPoints());
        dto.setCreatedAt(
            user.getCreatedAt() != null
                ? user.getCreatedAt().toString()
                : ""
        );
        return dto;
    }
}