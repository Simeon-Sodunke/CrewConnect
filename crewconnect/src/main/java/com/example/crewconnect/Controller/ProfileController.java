package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Admin;
import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.AdminRepository;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import com.example.crewconnect.View.UserProfileView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    private final EmployeeRepository employeeRepo;
    private final ManagerRepository managerRepo;
    private final AdminRepository adminRepo;

    public ProfileController(EmployeeRepository employeeRepo,
                             ManagerRepository managerRepo,
                             AdminRepository adminRepo) {
        this.employeeRepo = employeeRepo;
        this.managerRepo = managerRepo;
        this.adminRepo = adminRepo;
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        String principal = auth.getName(); // likely an email (see SecurityConfig.usernameParameter)
        Set<String> roles = auth.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        UserProfileView view;

        if (roles.contains("ROLE_ADMIN")) {
            Admin a = adminRepo.findByUsername(principal)
                    .orElseGet(() -> adminRepo.findByEmail(principal).orElseThrow(
                            () -> new IllegalStateException("Admin not found for " + principal)));
            view = UserProfileView.fromAdmin(a);

        } else if (roles.contains("ROLE_MANAGER")) {
            Manager m = managerRepo.findByUsername(principal)
                    .orElseGet(() -> managerRepo.findByEmail(principal).orElseThrow(
                            () -> new IllegalStateException("Manager not found for " + principal)));
            view = UserProfileView.fromManager(m);

        } else { // EMPLOYEE default
            Employee e = employeeRepo.findByUsername(principal)
                    .orElseGet(() -> employeeRepo.findByEmail(principal).orElseThrow(
                            () -> new IllegalStateException("Employee not found for " + principal)));
            view = UserProfileView.fromEmployee(e);
        }

        model.addAttribute("p", view);
        return "profile"; // templates/profile.html
    }

    // Optional alias if you want /admin/profile to also work
    @GetMapping("/admin/profile")
    public String adminProfile(Model model, Authentication auth) {
        return profile(model, auth);
    }
}