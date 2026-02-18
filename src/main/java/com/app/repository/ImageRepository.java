package com.app.repository;

import com.app.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByUploadDateAndActiveTrue(LocalDate uploadDate);

    boolean existsByUploadDate(LocalDate uploadDate);

    Optional<Image> findFirstByActiveTrueOrderByUploadDateDesc();
}