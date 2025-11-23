package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Availability;
import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Database.TimeRange;
import com.example.crewconnect.Repository.AvailabilityRepository;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import com.example.crewconnect.Repository.TimeRangeRepository;
import com.example.crewconnect.Service.PairingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AvailabilityController {

    private final EmployeeRepository employeeRepo;
    private final ManagerRepository managerRepo;
    private final AvailabilityRepository availabilityRepo;
    private final TimeRangeRepository timeRangeRepo;
    private final PairingService pairingService;

    public AvailabilityController(EmployeeRepository employeeRepo,
                                  ManagerRepository managerRepo,
                                  AvailabilityRepository availabilityRepo,
                                  TimeRangeRepository timeRangeRepo,
                                  PairingService pairingService) {
        this.employeeRepo = employeeRepo;
        this.managerRepo = managerRepo;
        this.availabilityRepo = availabilityRepo;
        this.timeRangeRepo = timeRangeRepo;
        this.pairingService = pairingService;
    }

    // =========================================================
    //  EMPLOYEE AVAILABILITY
    // =========================================================

    /**
     * Show "My Availability (Employee)" page.
     */
    @GetMapping("/employee/availability")
    public String employeeAvailabilityPage(Model model, Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("No logged-in user for /employee/availability");
        }

        Employee me = findEmployeeByLogin(principal.getName());

        // All time ranges for this employee’s availability, ordered by start
        List<TimeRange> slots =
                timeRangeRepo.findByAvailability_Employee_EmployeeIDAndEndAfterOrderByStartAsc(
                        me.getEmployeeID(), LocalDateTime.now()
                );

        model.addAttribute("slots", slots);
        return "employee-availability";
    }

    /**
     * Add a new availability window for the logged-in employee.
     */
    @PostMapping("/employee/availability/add")
    public String addEmployeeAvailability(
            @RequestParam("start")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime start,

            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime end,

            Principal principal) {

        if (principal == null) {
            throw new IllegalStateException("No logged-in user for /employee/availability/add");
        }

        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("Invalid start/end for availability window.");
        }

        Employee me = findEmployeeByLogin(principal.getName());

        // Ensure this employee has an Availability row
        Availability avail = availabilityRepo.findByEmployee(me)
                .orElseGet(() -> {
                    Availability a = new Availability();
                    a.setEmployee(me);
                    return availabilityRepo.save(a);
                });

        // Create TimeRange
        TimeRange tr = new TimeRange();
        tr.setAvailability(avail);
        tr.setStart(start);
        tr.setEnd(end);
        timeRangeRepo.save(tr);

        // Re-run auto pairing (employee <-> employee only)
        pairingService.autoPairNextDays(30);

        return "redirect:/employee/availability";
    }

    /**
     * Delete an availability window for the logged-in employee (if it belongs to them).
     */
    @PostMapping("/employee/availability/delete/{timeRangeId}")
    public String deleteEmployeeAvailability(@PathVariable("timeRangeId") Long timeRangeId,
                                             Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("No logged-in user for delete availability");
        }

        Employee me = findEmployeeByLogin(principal.getName());

        timeRangeRepo.findById(timeRangeId).ifPresent(tr -> {
            Availability avail = tr.getAvailability();
            Employee owner = (avail != null) ? avail.getEmployee() : null;

            if (owner != null && owner.getEmployeeID().equals(me.getEmployeeID())) {
                timeRangeRepo.delete(tr);
                // Re-run pairing after change
                pairingService.autoPairNextDays(30);
            } else {
                throw new IllegalArgumentException("You cannot delete another employee's availability.");
            }
        });

        return "redirect:/employee/availability";
    }

    // =========================================================
    //  MANAGER AVAILABILITY
    // =========================================================

    /**
     * Show "My Availability (Manager)" page.
     */
    @GetMapping("/manager/availability")
    public String managerAvailabilityPage(Model model, Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("No logged-in user for /manager/availability");
        }

        Manager mgr = findManagerByLogin(principal.getName());

        // All time ranges for this manager’s availability, ordered by start
        List<TimeRange> slots =
                timeRangeRepo.findByAvailability_Manager_ManagerIDAndEndAfterOrderByStartAsc(
                        mgr.getManagerID(), LocalDateTime.now()
                );

        model.addAttribute("slots", slots);
        return "manager-availability";
    }

    /**
     * Add a new availability window for the logged-in manager.
     * (We do NOT auto-pair managers; pairings are employee <-> employee only.)
     */
    @PostMapping("/manager/availability")
    public String addManagerAvailability(
            @RequestParam("start")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime start,

            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime end,

            Principal principal) {

        if (principal == null) {
            throw new IllegalStateException("No logged-in user for /manager/availability");
        }

        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("Invalid start/end for manager availability.");
        }

        Manager mgr = findManagerByLogin(principal.getName());

        Availability avail = availabilityRepo.findByManager(mgr)
                .orElseGet(() -> {
                    Availability a = new Availability();
                    a.setManager(mgr);
                    return availabilityRepo.save(a);
                });

        TimeRange tr = new TimeRange();
        tr.setAvailability(avail);
        tr.setStart(start);
        tr.setEnd(end);
        timeRangeRepo.save(tr);

        // No autoPair call here (only employees get auto-paired)
        return "redirect:/manager/availability";
    }

    // =========================================================
    //  Helper lookups
    // =========================================================

    private Employee findEmployeeByLogin(String login) {
        return employeeRepo.findByEmail(login)
                .or(() -> employeeRepo.findByUsername(login))
                .orElseThrow(() -> new IllegalArgumentException("Employee not found for login: " + login));
    }

    private Manager findManagerByLogin(String login) {
        return managerRepo.findByEmail(login)
                .or(() -> managerRepo.findByUsername(login))
                .orElseThrow(() -> new IllegalArgumentException("Manager not found for login: " + login));
    }
}