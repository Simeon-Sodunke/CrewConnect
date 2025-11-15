package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Pairing;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.NotificationRepository;
import com.example.crewconnect.Repository.PairingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DashboardController {

    private final PairingRepository pairingRepo;
    private final EmployeeRepository employeeRepo;
    private final NotificationRepository noteRepo;

    public DashboardController(PairingRepository pairingRepo,
                               EmployeeRepository employeeRepo,
                               NotificationRepository noteRepo) {
        this.pairingRepo = pairingRepo;
        this.employeeRepo = employeeRepo;
        this.noteRepo = noteRepo;
    }

    // Admin dashboard page
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Principal principal, Model model) {

        // 🔔 unread notifications for current user (if they are an Employee)
        if (principal != null) {
            employeeRepo.findByUsername(principal.getName()).ifPresent((Employee emp) -> {
                long unreadCount = noteRepo.countByEmployeeAndReadFlagFalse(emp);
                model.addAttribute("unreadCount", unreadCount);
            });
        }

        // Upcoming pairings for summary widget
        List<Pairing> upcomingPairings =
                pairingRepo.findByStartAfterOrderByStartAsc(LocalDateTime.now());
        model.addAttribute("upcomingPairings", upcomingPairings);

        return "admin-dashboard";
    }

    // Page that shows ALL upcoming pairings (for all employees & managers)
    @GetMapping("/admin/pairings")
    public String adminUpcomingPairings(Model model) {
        List<Pairing> upcomingPairings =
                pairingRepo.findByStartAfterOrderByStartAsc(LocalDateTime.now());

        model.addAttribute("pairings", upcomingPairings);
        return "admin-pairings"; // templates/admin-pairings.html
    }
}
