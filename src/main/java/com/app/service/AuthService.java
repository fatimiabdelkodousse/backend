package com.app.service;

import com.app.dto.LoginRequest;
import com.app.dto.LoginResponse;
import com.app.entity.Role;
import com.app.entity.User;
import com.app.repository.UserRepository;
import com.app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return new LoginResponse(
                    token,
                    user.getEmail(),
                    user.getRole().name(),
                    "Login successful"
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    // إنشاء الـ Admin الافتراضي عند بدء التطبيق
    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ROLE_ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("✅ Default Admin created: " + adminEmail);
            }
        };
    }
}