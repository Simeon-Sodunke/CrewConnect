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

        // 1) Find current employee
        Employee emp = employeeRepo.findByUsername(user.getUsername())
                .orElseThrow();

        // 2) Load notifications for this employee, newest first
        List<Notification> notifications =
                noteRepo.findByEmployeeOrderByCreatedAtDesc(emp);

        // 3) Mark them all as read (change to setSeen if your field name is different)
        notifications.forEach(n -> n.setReadFlag(true));
        noteRepo.saveAll(notifications);

        // 4) Add to model
        model.addAttribute("notifications", notifications);

        // Optional: for notifications.html "Back to Dashboard" role logic
        model.addAttribute("role", "EMPLOYEE"); // or derive from your Employee/Manager

        return "notifications";
    }
}