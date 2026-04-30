package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Restaurant r WHERE r.email IS NULL")
    void deleteByEmailIsNull();

    @Transactional
    @Modifying
    @Query("DELETE FROM Restaurant r WHERE r.password IS NULL")
    void deleteByPasswordIsNull();
}