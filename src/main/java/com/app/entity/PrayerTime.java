package com.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "prayer_times",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"country_code", "prayer_date"})
       })
public class PrayerTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Column(name = "country_name", length = 100)
    private String countryName;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "prayer_date", nullable = false)
    private LocalDate prayerDate;

    @Column(name = "maghreb_time", nullable = false)
    private LocalTime maghrebTime;

    @Column(name = "fajr_time")
    private LocalTime fajrTime;

    @Column(name = "dhuhr_time")
    private LocalTime dhuhrTime;

    @Column(name = "asr_time")
    private LocalTime asrTime;

    @Column(name = "isha_time")
    private LocalTime ishaTime;

    @Column(name = "notification_sent")
    private boolean notificationSent = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ==================== Constructors ====================

    public PrayerTime() {}

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCity() {
        return city;
    }

    public LocalDate getPrayerDate() {
        return prayerDate;
    }

    public LocalTime getMaghrebTime() {
        return maghrebTime;
    }

    public LocalTime getFajrTime() {
        return fajrTime;
    }

    public LocalTime getDhuhrTime() {
        return dhuhrTime;
    }

    public LocalTime getAsrTime() {
        return asrTime;
    }

    public LocalTime getIshaTime() {
        return ishaTime;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ==================== Setters ====================

    public void setId(Long id) {
        this.id = id;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPrayerDate(LocalDate prayerDate) {
        this.prayerDate = prayerDate;
    }

    public void setMaghrebTime(LocalTime maghrebTime) {
        this.maghrebTime = maghrebTime;
    }

    public void setFajrTime(LocalTime fajrTime) {
        this.fajrTime = fajrTime;
    }

    public void setDhuhrTime(LocalTime dhuhrTime) {
        this.dhuhrTime = dhuhrTime;
    }

    public void setAsrTime(LocalTime asrTime) {
        this.asrTime = asrTime;
    }

    public void setIshaTime(LocalTime ishaTime) {
        this.ishaTime = ishaTime;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}