package com.app.dto;

public class NotificationSettingsRequest {

    private boolean notificationsEnabled;
    private boolean messagesEnabled;
    private boolean maghrebEnabled;

    // ==================== Constructors ====================
    public NotificationSettingsRequest() {}

    // ==================== Getters ====================
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public boolean isMessagesEnabled() { return messagesEnabled; }
    public boolean isMaghrebEnabled() { return maghrebEnabled; }

    // ==================== Setters ====================
    public void setNotificationsEnabled(boolean v) { this.notificationsEnabled = v; }
    public void setMessagesEnabled(boolean v) { this.messagesEnabled = v; }
    public void setMaghrebEnabled(boolean v) { this.maghrebEnabled = v; }
}