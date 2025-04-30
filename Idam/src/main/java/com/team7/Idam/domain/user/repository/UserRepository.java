package com.team7.Idam.domain.user.repository;

import com.team7.Idam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // 로그인용

    boolean existsByEmail(String email); // e-mail 중복 방지
    boolean existsByPhone(String phone); // 전화번호 중복 방지
}

