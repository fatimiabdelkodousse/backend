package com.app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount;
                
                // محاولة القراءة من الـ classpath أولاً
                serviceAccount = getClass().getClassLoader()
                        .getResourceAsStream(credentialsPath);
                
                if (serviceAccount == null) {
                    // محاولة القراءة من المسار المباشر
                    serviceAccount = new FileInputStream(credentialsPath);
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully");
            }
        } catch (IOException e) {
            System.err.println("❌ Firebase initialization failed: " + e.getMessage());
            // لا نوقف التطبيق - نتابع بدون Firebase
        }
    }
}