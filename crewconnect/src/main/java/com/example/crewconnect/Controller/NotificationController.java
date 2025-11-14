package com.example.crewconnect.Controller;

import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.NotificationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class NotificationController {
    private final EmployeeRepository employeeRepo;
    private final NotificationRepository noteRepo;

    public NotificationController(EmployeeRepository employeeRepo, NotificationRepository noteRepo) {
        this.employeeRepo = employeeRepo;
        this.noteRepo = noteRepo;
    }

    @GetMapping("/notifications")
    public String notifications(Model model, Principal principal) {
        var me = employeeRepo.findByEmail(principal.getName())
                .or(() -> employeeRepo.findByUsername(principal.getName()))
                .orElseThrow();
        model.addAttribute("notes", noteRepo.findByEmployeeOrderByCreatedAtDesc(me));
        return "notifications";
    }
}