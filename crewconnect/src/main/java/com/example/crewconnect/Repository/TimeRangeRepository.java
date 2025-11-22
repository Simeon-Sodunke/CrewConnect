package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.TimeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeRangeRepository extends JpaRepository<TimeRange, Long> {

    List<TimeRange> findByAvailability_Employee_EmployeeIDOrderByStartAsc(Long employeeId);

    List<TimeRange> findByAvailability_Employee_EmployeeIDAndEndAfterOrderByStartAsc(
            Long employeeId, LocalDateTime now);

    List<TimeRange> findByAvailability_Manager_ManagerIDOrderByStartAsc(Long managerId);

    List<TimeRange> findByAvailability_Manager_ManagerIDAndEndAfterOrderByStartAsc(
            Long managerId, LocalDateTime now);

    List<TimeRange> findAllByEndAfterAndStartBefore(LocalDateTime from, LocalDateTime to);

    List<TimeRange> findAllByStartBetween(LocalDateTime from, LocalDateTime to);
}