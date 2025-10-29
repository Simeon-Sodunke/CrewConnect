package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUsername(String username);
    Optional<Manager> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Admin→Managers
    List<Manager> findByAdmin_AdminID(Long adminId);
}
