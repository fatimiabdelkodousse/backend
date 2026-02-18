package com.app.dto;

import java.time.LocalDate;
public class PrayerTimeResponse {

    private String countryCode;
    private String countryName;
    private String city;
    private LocalDate prayerDate;
    private String maghrebTime;
    private String fajrTime;
    private String dhuhrTime;
    private String asrTime;
    private String ishaTime;
    private long secondsUntilMaghreb;

    // ==================== Constructors ====================

    public PrayerTimeResponse() {}

    // ==================== Getters ====================

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

    public String getMaghrebTime() {
        return maghrebTime;
    }

    public String getFajrTime() {
        return fajrTime;
    }

    public String getDhuhrTime() {
        return dhuhrTime;
    }

    public String getAsrTime() {
        return asrTime;
    }

    public String getIshaTime() {
        return ishaTime;
    }

    public long getSecondsUntilMaghreb() {
        return secondsUntilMaghreb;
    }

    // ==================== Setters ====================

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

    public void setMaghrebTime(String maghrebTime) {
        this.maghrebTime = maghrebTime;
    }

    public void setFajrTime(String fajrTime) {
        this.fajrTime = fajrTime;
    }

    public void setDhuhrTime(String dhuhrTime) {
        this.dhuhrTime = dhuhrTime;
    }

    public void setAsrTime(String asrTime) {
        this.asrTime = asrTime;
    }

    public void setIshaTime(String ishaTime) {
        this.ishaTime = ishaTime;
    }

    public void setSecondsUntilMaghreb(long secondsUntilMaghreb) {
        this.secondsUntilMaghreb = secondsUntilMaghreb;
    }
}