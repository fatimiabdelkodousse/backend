package com.app.config;

import com.app.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // تعطيل CSRF لأننا نستخدم JWT
            .csrf(csrf -> csrf.disable())
            
            // إعداد CORS
            .cors(cors -> cors.configure(http))
            
            // إعداد الـ Session كـ Stateless
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // إعداد الـ Endpoints
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - لا تحتاج تسجيل دخول
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/prayer/public/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                
                // Admin endpoints - فقط ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                
                // User endpoints - فقط USER
                .requestMatchers("/api/user/**").hasAuthority("ROLE_USER")
                
                // أي endpoint آخر يحتاج تسجيل دخول
                .anyRequest().authenticated()
            )
            
            // إضافة JWT Filter قبل UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}