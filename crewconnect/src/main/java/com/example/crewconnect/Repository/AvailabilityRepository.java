package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.Availability;
import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByEmployee(Employee e);
    List<Availability> findByManager(Manager m);
}