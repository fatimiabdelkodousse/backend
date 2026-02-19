package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class SendMessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 500, message = "Message too long")
    private String content;

    // ALL أو SPECIFIC
    @NotNull(message = "Target type is required")
    private String targetType;

    // قائمة IDs المستخدمين (فقط عند SPECIFIC)
    private List<Long> userIds;

    // ==================== Constructors ====================
    public SendMessageRequest() {}

    // ==================== Getters ====================
    public String getContent() { return content; }
    public String getTargetType() { return targetType; }
    public List<Long> getUserIds() { return userIds; }

    // ==================== Setters ====================
    public void setContent(String content) { this.content = content; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
}