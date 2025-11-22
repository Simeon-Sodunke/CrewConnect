package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.AvailabilityRepository;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import com.example.crewconnect.Repository.PairingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportsController {

    private final EmployeeRepository employeeRepo;
    private final ManagerRepository managerRepo;
    private final AvailabilityRepository availabilityRepo;
    private final PairingRepository pairingRepo;

    public ReportsController(EmployeeRepository employeeRepo,
                             ManagerRepository managerRepo,
                             AvailabilityRepository availabilityRepo,
                             PairingRepository pairingRepo) {
        this.employeeRepo = employeeRepo;
        this.managerRepo = managerRepo;
        this.availabilityRepo = availabilityRepo;
        this.pairingRepo = pairingRepo;
    }

    @GetMapping("/admin/reports")
    public String adminReports(Model model) {

        // Basic totals
        long totalEmployees    = employeeRepo.count();
        long totalManagers     = managerRepo.count();
        long totalPairings     = pairingRepo.count();
        long totalAvailability = availabilityRepo.count();  // total availability windows

        // 🔹 "Recently registered users" (approximate: last 10 IDs)
        int N = 10;

        long recentEmployees = 0;
        Employee lastEmp = employeeRepo.findTopByOrderByEmployeeIDDesc();
        if (lastEmp != null) {
            long maxEmpId   = lastEmp.getEmployeeID();
            long threshold  = Math.max(1, maxEmpId - (N - 1)); // last N employees
            recentEmployees = employeeRepo.countByEmployeeIDGreaterThan(threshold - 1);
        }

        long recentManagers = 0;
        Manager lastMgr = managerRepo.findTopByOrderByManagerIDDesc();
        if (lastMgr != null) {
            long maxMgrId   = lastMgr.getManagerID();
            long threshold  = Math.max(1, maxMgrId - (N - 1)); // last N managers
            recentManagers  = managerRepo.countByManagerIDGreaterThan(threshold - 1);
        }

        long recentUsers = recentEmployees + recentManagers;
        String recentLabel = "Approx. last " + N + " registered users";

        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("totalPairings", totalPairings);
        model.addAttribute("totalAvailability", totalAvailability);
        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("recentLabel", recentLabel);

        return "admin-reports";
    }
}