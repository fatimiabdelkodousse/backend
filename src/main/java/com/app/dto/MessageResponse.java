package com.app.dto;

import java.time.LocalDateTime;

public class MessageResponse {

    private Long id;
    private Long recipientId;
    private String content;
    private String sentBy;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    // ==================== Constructors ====================
    public MessageResponse() {}

    // ==================== Getters ====================
    public Long getId() { return id; }
    public Long getRecipientId() { return recipientId; }
    public String getContent() { return content; }
    public String getSentBy() { return sentBy; }
    public boolean isRead() { return read; }
    public LocalDateTime getReadAt() { return readAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ==================== Setters ====================
    public void setId(Long id) { this.id = id; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }
    public void setContent(String content) { this.content = content; }
    public void setSentBy(String sentBy) { this.sentBy = sentBy; }
    public void setRead(boolean read) { this.read = read; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}