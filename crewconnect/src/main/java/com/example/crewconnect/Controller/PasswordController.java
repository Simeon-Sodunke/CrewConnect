package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/password")
public class PasswordController {

    private final EmployeeRepository employeeRepo;
    private final ManagerRepository managerRepo;
    private final PasswordEncoder passwordEncoder;

    public PasswordController(EmployeeRepository employeeRepo,
                              ManagerRepository managerRepo,
                              PasswordEncoder passwordEncoder) {
        this.employeeRepo = employeeRepo;
        this.managerRepo = managerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/set")
    public String showSetPasswordForm() {
        return "password-set";
    }

    @PostMapping("/set")
    public String handleSetPassword(@RequestParam("password") String password,
                                    @RequestParam("confirmPassword") String confirmPassword,
                                    Principal principal,
                                    Model model) {

        // 1) Must be logged in
        if (principal == null) {
            return "redirect:/login";
        }

        // 2) Basic validations
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "password-set";
        }

        if (password.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters long.");
            return "password-set";
        }

        String login = principal.getName(); // email or username

        // 3) Try Employee first
        Optional<Employee> empOpt = employeeRepo.findByEmail(login)
                .or(() -> employeeRepo.findByUsername(login));

        if (empOpt.isPresent()) {
            Employee e = empOpt.get();

            // 4) Prevent reusing same password
            if (passwordEncoder.matches(password, e.getPassword())) {
                model.addAttribute("error", "New password must be different from your current password.");
                return "password-set";
            }

            // 5) Save new password
            e.setPassword(passwordEncoder.encode(password));
            e.setMustChangePassword(false);
            employeeRepo.save(e);

        } else {
            // Manager fallback
            Manager m = managerRepo.findByEmail(login)
                    .or(() -> managerRepo.findByUsername(login))
                    .orElseThrow(() -> new IllegalStateException("User not found: " + login));

            if (passwordEncoder.matches(password, m.getPassword())) {
                model.addAttribute("error", "New password must be different from your current password.");
                return "password-set";
            }

            m.setPassword(passwordEncoder.encode(password));
            m.setMustChangePassword(false);
            managerRepo.save(m);
        }

        // 6) Force re-login with new password
        return "redirect:/login?passwordChanged";
    }
}