package com.app.dto;

public class UserProfileResponse {

    private Long id;
    private String email;
    private int points;
    private boolean answeredToday;
    private Boolean todayAnswer;

    // ==================== Constructors ====================
    public UserProfileResponse() {}

    // ==================== Getters ====================
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public int getPoints() { return points; }
    public boolean isAnsweredToday() { return answeredToday; }
    public Boolean getTodayAnswer() { return todayAnswer; }

    // ==================== Setters ====================
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPoints(int points) { this.points = points; }
    public void setAnsweredToday(boolean answeredToday) {
        this.answeredToday = answeredToday;
    }
    public void setTodayAnswer(Boolean todayAnswer) {
        this.todayAnswer = todayAnswer;
    }
}