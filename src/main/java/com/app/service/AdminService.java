package com.app.service;

import com.app.dto.CreateUserRequest;
import com.app.entity.Role;
import com.app.entity.User;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findByRole(Role.ROLE_USER);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new RuntimeException("Cannot delete admin user");
        }
        
        userRepository.delete(user);
    }

    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }
}