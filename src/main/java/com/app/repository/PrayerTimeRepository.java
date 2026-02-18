package com.app.repository;

import com.app.entity.PrayerTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrayerTimeRepository extends JpaRepository<PrayerTime, Long> {

    Optional<PrayerTime> findByCountryCodeAndPrayerDate(
            String countryCode, LocalDate prayerDate);

    List<PrayerTime> findByPrayerDateAndNotificationSentFalse(LocalDate prayerDate);

    @Query("SELECT p FROM PrayerTime p WHERE p.prayerDate = :date")
    List<PrayerTime> findAllByDate(LocalDate date);

    boolean existsByCountryCodeAndPrayerDate(String countryCode, LocalDate prayerDate);
}