package com.app.dto;

public class KarshAnswerResponse {

    private boolean answeredYes;
    private int pointsChanged;
    private int totalPoints;
    private String message;
    private boolean alreadyAnswered;

    // ==================== Constructors ====================
    public KarshAnswerResponse() {}

    // ==================== Getters ====================
    public boolean isAnsweredYes() { return answeredYes; }
    public int getPointsChanged() { return pointsChanged; }
    public int getTotalPoints() { return totalPoints; }
    public String getMessage() { return message; }
    public boolean isAlreadyAnswered() { return alreadyAnswered; }

    // ==================== Setters ====================
    public void setAnsweredYes(boolean answeredYes) {
        this.answeredYes = answeredYes;
    }
    public void setPointsChanged(int pointsChanged) {
        this.pointsChanged = pointsChanged;
    }
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setAlreadyAnswered(boolean alreadyAnswered) {
        this.alreadyAnswered = alreadyAnswered;
    }
}