package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@EnableScheduling
public class AppApplication {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    // تعريف endpoint لخدمة الصور المرفوعة
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        String dir = uploadDir;
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + dir + "/");
            }
        };
    }
}