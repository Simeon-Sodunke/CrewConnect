package com.example.crewconnect.Service;

import com.example.crewconnect.Database.Availability;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Database.TimeRange;
import com.example.crewconnect.Repository.AvailabilityRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import com.example.crewconnect.Repository.TimeRangeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepo;
    private final ManagerRepository managerRepo;
    private final TimeRangeRepository timeRangeRepo;

    public AvailabilityService(AvailabilityRepository availabilityRepo,
                               ManagerRepository managerRepo,
                               TimeRangeRepository timeRangeRepo) {
        this.availabilityRepo = availabilityRepo;
        this.managerRepo = managerRepo;
        this.timeRangeRepo = timeRangeRepo;
    }

    /* ------------- MANAGER --------------- */
    public void addWindowForManager(String login, LocalDateTime start, LocalDateTime end) {
        // 1) find the manager by login (email or username)
        Manager m = managerRepo.findByEmail(login)
                .or(() -> managerRepo.findByUsername(login))
                .orElseThrow(() -> new IllegalArgumentException("Manager not found: " + login));

        // 2) get (or create) the manager's Availability record
        Availability avail1 = availabilityRepo.findByManager(m).stream()
                .findFirst()
                .orElseGet(() -> {
                    Availability a = new Availability();
                    a.setManager(m);
                    return availabilityRepo.save(a);
                });

        // 3) create & attach the time range
        TimeRange tr = new TimeRange();
        tr.setStart(start);
        tr.setEnd(end);
        tr.setAvailability(avail1);   // <- IMPORTANT: attach the owning side
        // If your Availability has a helper like avail1.addRange(tr), you can call that instead.

        // 4) save
        timeRangeRepo.save(tr);
    }
}