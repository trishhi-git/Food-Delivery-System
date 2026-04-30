package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // useful for login later
    Optional<User> findByEmail(String email);
}
