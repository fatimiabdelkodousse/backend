package com.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "sent_by")
    private String sentBy;

    // ALL = لجميع المستخدمين, SPECIFIC = لمستخدمين محددين
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private MessageTargetType targetType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // العلاقة مع المستلمين
    @OneToMany(mappedBy = "message",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<MessageRecipient> recipients = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ==================== Constructors ====================
    public Message() {}

    // ==================== Getters ====================
    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getSentBy() { return sentBy; }
    public MessageTargetType getTargetType() { return targetType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<MessageRecipient> getRecipients() { return recipients; }

    // ==================== Setters ====================
    public void setId(Long id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setSentBy(String sentBy) { this.sentBy = sentBy; }
    public void setTargetType(MessageTargetType targetType) { this.targetType = targetType; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setRecipients(List<MessageRecipient> recipients) { this.recipients = recipients; }
}