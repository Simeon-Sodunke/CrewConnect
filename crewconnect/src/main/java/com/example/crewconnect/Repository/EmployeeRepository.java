package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // you may already have these
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByUsername(String username);

    // NEW: get all employees assigned to a manager id
    List<Employee> findByManager_ManagerID(Long managerId);
}

