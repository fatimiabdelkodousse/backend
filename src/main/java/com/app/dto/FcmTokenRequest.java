package com.app.dto;

import jakarta.validation.constraints.NotBlank;

public class FcmTokenRequest {

    @NotBlank(message = "FCM token is required")
    private String token;

    private String deviceType;

    // ==================== Constructors ====================

    public FcmTokenRequest() {}

    // ==================== Getters ====================

    public String getToken() {
        return token;
    }

    public String getDeviceType() {
        return deviceType;
    }

    // ==================== Setters ====================

    public void setToken(String token) {
        this.token = token;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}