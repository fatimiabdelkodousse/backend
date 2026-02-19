package com.app.dto;

public class UserDTO {

    private Long id;
    private String email;
    private String role;
    private boolean enabled;
    private int points;
    private String createdAt;

    // ==================== Constructors ====================
    public UserDTO() {}

    // ==================== Getters ====================
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public int getPoints() { return points; }
    public String getCreatedAt() { return createdAt; }

    // ==================== Setters ====================
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setPoints(int points) { this.points = points; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}