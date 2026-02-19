package com.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings")
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "notifications_enabled")
    private boolean notificationsEnabled = true;

    @Column(name = "messages_enabled")
    private boolean messagesEnabled = true;

    @Column(name = "maghreb_enabled")
    private boolean maghrebEnabled = true;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== Constructors ====================
    public NotificationSettings() {}

    // ==================== Getters ====================
    public Long getId() { return id; }
    public User getUser() { return user; }
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public boolean isMessagesEnabled() { return messagesEnabled; }
    public boolean isMaghrebEnabled() { return maghrebEnabled; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ==================== Setters ====================
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    public void setMessagesEnabled(boolean messagesEnabled) {
        this.messagesEnabled = messagesEnabled;
    }
    public void setMaghrebEnabled(boolean maghrebEnabled) {
        this.maghrebEnabled = maghrebEnabled;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}