package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.TimeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeRangeRepository extends JpaRepository<TimeRange, Long> {

    // Employee's own availability slots (employee view)
    List<TimeRange> findByAvailability_Employee_EmployeeIDOrderByStartAsc(Long employeeId);

    // PairingService: all ranges that overlap a window [from, to)
    List<TimeRange> findAllByEndAfterAndStartBefore(LocalDateTime from, LocalDateTime to);

    // Admin availability page: all ranges that START within a window [from, to)
    List<TimeRange> findAllByStartBetween(LocalDateTime from, LocalDateTime to);

    List<TimeRange> findByAvailability_Manager_ManagerIDOrderByStartAsc(Long managerId);
}