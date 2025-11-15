package com.example.crewconnect.Repository;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByEmployeeAndReadFlagFalse(Employee employee);

    void deleteAllByEmployee(Employee employee);

    List<Notification> findByEmployeeOrderByCreatedAtDesc(Employee employee);
}
