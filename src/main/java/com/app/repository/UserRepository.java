package com.app.repository;

import com.app.entity.Role;
import com.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByEnabledTrue();

    // ✅ أضف هذا
    List<User> findByRoleAndEnabledTrue(Role role);
}