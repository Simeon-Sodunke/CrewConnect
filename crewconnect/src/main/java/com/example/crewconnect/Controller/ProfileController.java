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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    private final EmployeeRepository employeeRepo;
    private final ManagerRepository managerRepo;
    private final AdminRepository adminRepo;
    private final PasswordEncoder encoder;   // 🔹 NEW

    public ProfileController(EmployeeRepository employeeRepo,
                             ManagerRepository managerRepo,
                             AdminRepository adminRepo,
                             PasswordEncoder encoder) {   // 🔹 NEW ARG
        this.employeeRepo = employeeRepo;
        this.managerRepo = managerRepo;
        this.adminRepo = adminRepo;
        this.encoder = encoder;              // 🔹 NEW
    }

    // ========== VIEW PROFILE (GET) ==========

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        String principal = auth.getName(); // username or email
        Set<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

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

    // admin profile
    @GetMapping("/admin/profile")
    public String adminProfile(Model model, Authentication auth) {
        return profile(model, auth);
    }

    // ========== UPDATE PROFILE + PASSWORD (POST) ==========

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam(name = "phonenumber", required = false) String phoneNumber,
            @RequestParam(name = "address",     required = false) String address,
            @RequestParam(name = "fullName",    required = false) String fullName,
            @RequestParam(name = "email",       required = false) String email,
            @RequestParam(name = "password",    required = false) String password,
            Authentication auth,
            RedirectAttributes ra) {  // ✅ IMPORTANT

        String principal = auth.getName();
        Set<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // ADMIN: only phone/address (no password change)
        if (roles.contains("ROLE_ADMIN")) {
            Admin a = adminRepo.findByUsername(principal)
                    .orElseGet(() -> adminRepo.findByEmail(principal).orElseThrow(
                            () -> new IllegalStateException("Admin not found for " + principal)));

            if (phoneNumber != null && !phoneNumber.isBlank()) {
                a.setPhonenumber(phoneNumber);
            }
            if (address != null && !address.isBlank()) {
                a.setAddress(address);
            }
            adminRepo.save(a);
            ra.addFlashAttribute("successMessage", "Profile updated successfully.");
            return "redirect:/profile";
        }

        // MANAGER
        if (roles.contains("ROLE_MANAGER")) {
            Manager m = managerRepo.findByUsername(principal)
                    .orElseGet(() -> managerRepo.findByEmail(principal).orElseThrow(
                            () -> new IllegalStateException("Manager not found for " + principal)));

            // 🔹 PASSWORD LOGIC
            if (password != null && !password.isBlank()) {
                // if they typed the same password as current one
                if (encoder.matches(password, m.getPassword())) {
                    ra.addFlashAttribute("errorMessage",
                            "New password must be different from your current password.");
                    return "redirect:/profile";   // 🔴 EARLY RETURN ON ERROR
                }

                // otherwise update
                m.setPassword(encoder.encode(password));
                m.setMustChangePassword(false);
                ra.addFlashAttribute("successMessage", "Password updated successfully.");
            } else {
                // just profile fields, no password
                ra.addFlashAttribute("successMessage", "Profile updated successfully.");
            }

            if (phoneNumber != null && !phoneNumber.isBlank()) {
                m.setPhonenumber(phoneNumber);
            }
            if (address != null && !address.isBlank()) {
                m.setAddress(address);
            }
            managerRepo.save(m);
            return "redirect:/profile";
        }

        // EMPLOYEE (default)
        Employee e = employeeRepo.findByUsername(principal)
                .orElseGet(() -> employeeRepo.findByEmail(principal).orElseThrow(
                        () -> new IllegalStateException("Employee not found for " + principal)));

        // 🔹 PASSWORD LOGIC
        if (password != null && !password.isBlank()) {
            if (encoder.matches(password, e.getPassword())) {
                ra.addFlashAttribute("errorMessage",
                        "New password must be different from your current password.");
                return "redirect:/profile";   // 🔴 EARLY RETURN ON ERROR
            }

            e.setPassword(encoder.encode(password));
            e.setMustChangePassword(false);
            ra.addFlashAttribute("successMessage", "Password updated successfully.");
        } else {
            ra.addFlashAttribute("successMessage", "Profile updated successfully.");
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            e.setPhonenumber(phoneNumber);
        }
        if (address != null && !address.isBlank()) {
            e.setAddress(address);
        }
        employeeRepo.save(e);

        return "redirect:/profile";
    }
}