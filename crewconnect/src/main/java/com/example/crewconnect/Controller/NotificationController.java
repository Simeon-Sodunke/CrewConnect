package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Notification;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.NotificationRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NotificationController {

    private final NotificationRepository noteRepo;
    private final EmployeeRepository employeeRepo;

    public NotificationController(NotificationRepository noteRepo,
                                  EmployeeRepository employeeRepo) {
        this.noteRepo = noteRepo;
        this.employeeRepo = employeeRepo;
    }

    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal UserDetails user,
                                Model model) {

        if (user == null) {
            throw new IllegalStateException("No logged in user for /notifications");
        }

        var optEmp = employeeRepo.findByEmail(user.getUsername())
                .or(() -> employeeRepo.findByUsername(user.getUsername()));

        if (optEmp.isEmpty()) {
            // Logged in user is NOT an employee (likely a Manager)
            model.addAttribute("notifications", List.of());
            model.addAttribute("role", "MANAGER");
            return "notifications";
        }

        Employee emp = optEmp.get();

        List<Notification> notifications =
                noteRepo.findByEmployeeOrderByCreatedAtDesc(emp);

        notifications.forEach(n -> n.setReadFlag(true));
        noteRepo.saveAll(notifications);

        model.addAttribute("notifications", notifications);
        model.addAttribute("role", "EMPLOYEE");
        return "notifications";
    }
}