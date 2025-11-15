package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.NotificationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeRepository employeeRepo;
    private final NotificationRepository noteRepo;

    public EmployeeController(EmployeeRepository employeeRepo,
                              NotificationRepository noteRepo) {
        this.employeeRepo = employeeRepo;
        this.noteRepo = noteRepo;
    }

    // GET /employee  → Employee dashboard
    @GetMapping("")
    public String employeeDashboard(Principal principal, Model model) {

        // 🔔 unread notifications for this employee
        if (principal != null) {
            Employee emp = employeeRepo.findByUsername(principal.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Employee not found: " + principal.getName()));

            long unreadCount = noteRepo.countByEmployeeAndReadFlagFalse(emp);
            model.addAttribute("unreadCount", unreadCount);
        }

        model.addAttribute("pageTitle", "Employee Dashboard");
        return "employee-dashboard";
    }
}