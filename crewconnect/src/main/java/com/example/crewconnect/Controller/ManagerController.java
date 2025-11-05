package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ManagerController {

    private final ManagerRepository managerRepo;
    private final EmployeeRepository employeeRepo;

    public ManagerController(ManagerRepository managerRepo,
                             EmployeeRepository employeeRepo) {
        this.managerRepo = managerRepo;
        this.employeeRepo = employeeRepo;
    }

    /** Manager dashboard (you likely already have this mapped elsewhere) */
    @GetMapping("/manager")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Manager Dashboard");
        return "manager-dashboard";
    }

    /** List employees assigned to the logged-in manager */
    @GetMapping("/manager/employees")
    public String myEmployees(Principal principal, Model model) {
        // principal.getName() = username of the logged-in manager
        Manager me = managerRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Manager not found: " + principal.getName()));

        model.addAttribute("manager", me);
        model.addAttribute("employees", employeeRepo.findByManager_ManagerID(me.getManagerID()));
        model.addAttribute("pageTitle", "My Employees");
        return "manager-employees"; // templates/manager-employees.html
    }
}