package com.example.crewconnect.Service;

import com.example.crewconnect.Database.*;
import com.example.crewconnect.Repository.*;
import org.springframework.stereotype.Service;

import com.example.crewconnect.Database.Availability;
import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Repository.AvailabilityRepository;
import com.example.crewconnect.Repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvailabilityService {

    private final EmployeeRepository employeeRepo;
    private final ManagerRepository managerRepo;
    private final AvailabilityRepository availabilityRepo;

    public AvailabilityService(EmployeeRepository employeeRepo,
                               ManagerRepository managerRepo,
                               AvailabilityRepository availabilityRepo) {
        this.employeeRepo = employeeRepo;
        this.managerRepo  = managerRepo;
        this.availabilityRepo = availabilityRepo;
    }

    // Employee
    public void addWindowForEmployee(String login, LocalDateTime start, LocalDateTime end) {
        Employee e = employeeRepo.findByEmail(login)
                .or(() -> employeeRepo.findByUsername(login))
                .orElseThrow();

        Availability avail = availabilityRepo.findByEmployee(e).stream()
                .findFirst().orElseGet(() -> {
                    Availability a = new Availability();
                    a.setEmployee(e);
                    return availabilityRepo.save(a);
                });

        TimeRange tr = new TimeRange();
        tr.setStart(start);
        tr.setEnd(end);
        tr.setAvailability(avail);
        avail.getWindow().add(tr);

        availabilityRepo.save(avail);
    }

    // Manager
    public void addWindowForManager(String login, LocalDateTime start, LocalDateTime end) {
        Manager m = managerRepo.findByEmail(login)
                .or(() -> managerRepo.findByUsername(login))
                .orElseThrow();

        Availability avail = availabilityRepo.findByManager(m).stream()
                .findFirst().orElseGet(() -> {
                    Availability a = new Availability();
                    a.setManager(m);
                    return availabilityRepo.save(a);
                });

        TimeRange tr = new TimeRange();
        tr.setStart(start);
        tr.setEnd(end);
        tr.setAvailability(avail);
        avail.getWindow().add(tr);

        availabilityRepo.save(avail);
    }

    public List<TimeRange> getEmployeeSlots(String login) {
        return employeeRepo.findByEmail(login)
                .or(() -> employeeRepo.findByUsername(login))
                .map(availabilityRepo::findByEmployee)
                .orElse(List.of())
                .stream().findFirst().map(Availability::getWindow).orElse(List.of());
    }

    public List<TimeRange> getManagerSlots(String login) {
        return managerRepo.findByEmail(login)
                .or(() -> managerRepo.findByUsername(login))
                .map(availabilityRepo::findByManager)
                .orElse(List.of())
                .stream().findFirst().map(Availability::getWindow).orElse(List.of());
    }
}