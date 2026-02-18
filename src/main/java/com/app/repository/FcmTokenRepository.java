package com.app.repository;

import com.app.entity.FcmToken;
import com.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByToken(String token);

    List<FcmToken> findByUserAndActiveTrue(User user);

    List<FcmToken> findByActiveTrue();

    boolean existsByToken(String token);

    void deleteByToken(String token);
}