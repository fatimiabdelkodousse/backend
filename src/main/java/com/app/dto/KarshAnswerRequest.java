package com.app.dto;

import jakarta.validation.constraints.NotNull;

public class KarshAnswerRequest {

    @NotNull(message = "Answer is required")
    private Boolean answeredYes;

    // ==================== Constructors ====================
    public KarshAnswerRequest() {}

    // ==================== Getters ====================
    public Boolean getAnsweredYes() { return answeredYes; }

    // ==================== Setters ====================
    public void setAnsweredYes(Boolean answeredYes) {
        this.answeredYes = answeredYes;
    }
}