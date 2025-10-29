package com.example.crewconnect.Controller;

import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/password")
public class PasswordController {

    private final EmployeeRepository empRepo;
    private final ManagerRepository mgrRepo;
    private final PasswordEncoder encoder;

    public PasswordController(EmployeeRepository empRepo,
                              ManagerRepository mgrRepo,
                              PasswordEncoder encoder) {
        this.empRepo = empRepo;
        this.mgrRepo = mgrRepo;
        this.encoder = encoder;
    }

    @GetMapping("/set")
    public String showSetForm(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "password-set";              // looks for templates/password-set.html
    }

    @PostMapping("/set")
    public String handleSet(@RequestParam String newPassword,
                            @RequestParam String confirmPassword,
                            Authentication auth,
                            RedirectAttributes ra) {

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/password/set";
        }

        String username = auth.getName();
        // Try manager first
        Manager m = mgrRepo.findByUsername(username).orElse(null);
        if (m != null) {
            m.setPassword(encoder.encode(newPassword));
            m.setMustChangePassword(false);
            mgrRepo.save(m);
            ra.addFlashAttribute("msg", "Password updated.");
            return "redirect:/manager";
        }

        // Then employee
        Employee e = empRepo.findByUsername(username).orElse(null);
        if (e != null) {
            e.setPassword(encoder.encode(newPassword));
            e.setMustChangePassword(false);
            empRepo.save(e);
            ra.addFlashAttribute("msg", "Password updated.");
            return "redirect:/employee";
        }

        ra.addFlashAttribute("error", "User not found.");
        return "redirect:/";
    }
}