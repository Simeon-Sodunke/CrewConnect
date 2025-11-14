package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.Availability;
import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    Optional<Availability> findByEmployee(Employee employee);

    // ✅ Make sure THIS returns Optional, not List
    Optional<Availability> findByManager(Manager manager);

    // keep any other methods you already have here
}