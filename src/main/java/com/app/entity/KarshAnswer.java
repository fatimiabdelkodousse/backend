package com.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "karsh_answers",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "answer_date"})
       })
public class KarshAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "answer_date", nullable = false)
    private LocalDate answerDate;

    // true = نعم عبيت, false = لا
    @Column(name = "answered_yes", nullable = false)
    private boolean answeredYes;

    @Column(name = "points_changed", nullable = false)
    private int pointsChanged;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ==================== Constructors ====================

    public KarshAnswer() {}

    // ==================== Getters ====================

    public Long getId() { return id; }
    public User getUser() { return user; }
    public LocalDate getAnswerDate() { return answerDate; }
    public boolean isAnsweredYes() { return answeredYes; }
    public int getPointsChanged() { return pointsChanged; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ==================== Setters ====================

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAnswerDate(LocalDate answerDate) { this.answerDate = answerDate; }
    public void setAnsweredYes(boolean answeredYes) { this.answeredYes = answeredYes; }
    public void setPointsChanged(int pointsChanged) { this.pointsChanged = pointsChanged; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}