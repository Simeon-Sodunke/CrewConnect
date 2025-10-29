package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Manager→Employees
    List<Employee> findByManager_ManagerID(Long managerId);
}

