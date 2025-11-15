package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import com.example.crewconnect.Repository.NotificationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ManagerController {

    private final ManagerRepository managerRepo;
    private final EmployeeRepository employeeRepo;
    private final NotificationRepository noteRepo;

    public ManagerController(ManagerRepository managerRepo,
                             EmployeeRepository employeeRepo,
                             NotificationRepository noteRepo) {
        this.managerRepo = managerRepo;
        this.employeeRepo = employeeRepo;
        this.noteRepo = noteRepo;
    }

    /** Manager dashboard */
    @GetMapping("/manager")
    public String dashboard(Principal principal, Model model) {

        // 🔔 unread notifications for this user if they also exist as Employee
        if (principal != null) {
            employeeRepo.findByUsername(principal.getName()).ifPresent((Employee emp) -> {
                long unreadCount = noteRepo.countByEmployeeAndReadFlagFalse(emp);
                model.addAttribute("unreadCount", unreadCount);
            });
        }

        model.addAttribute("pageTitle", "Manager Dashboard");
        return "manager-dashboard";
    }

    /** List employees assigned to the logged-in manager */
    @GetMapping("/manager/employees")
    public String myEmployees(Principal principal, Model model) {
        // principal.getName() = username of the logged-in manager
        Manager me = managerRepo.findByUsername(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Manager not found: " + principal.getName()));

        // 🔔 keep bell working on this page too (if manager is also in Employee table)
        employeeRepo.findByUsername(principal.getName()).ifPresent((Employee emp) -> {
            long unreadCount = noteRepo.countByEmployeeAndReadFlagFalse(emp);
            model.addAttribute("unreadCount", unreadCount);
        });

        model.addAttribute("manager", me);
        model.addAttribute("employees", employeeRepo.findByManager_ManagerID(me.getManagerID()));
        model.addAttribute("pageTitle", "My Employees");
        return "manager-employees"; // templates/manager-employees.html
    }
}