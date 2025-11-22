package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Pairing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PairingRepository extends JpaRepository<Pairing, Long> {

    // For "My Pairings" view
    List<Pairing> findByEmployeeAOrEmployeeBOrderByStartDesc(Employee a, Employee b);

    // Avoid duplicates
    boolean existsByEmployeeAAndEmployeeBAndStartAndEnd(Employee a, Employee b,
                                                        LocalDateTime start, LocalDateTime end);

    // For cleanup when deleting an employee
    void deleteAllByEmployeeA(Employee employee);
    void deleteAllByEmployeeB(Employee employee);

    // ✅ NEW: get all upcoming pairings for everyone, sorted soonest first
    List<Pairing> findByStartAfterOrderByStartAsc(LocalDateTime from);

    List<Pairing> findByStatusAndEndBefore(String status, LocalDateTime time);
}