package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findByUsername(String username);
    Optional<Manager> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // 🔹 NEW: for "recently registered users" (based on ManagerID)
    Manager findTopByOrderByManagerIDDesc();

    long countByManagerIDGreaterThan(Long managerID);
}