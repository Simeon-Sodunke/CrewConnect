package com.example.crewconnect.Controller;

import com.example.crewconnect.Service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class AvailabilityController {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    // Employee pages
    @GetMapping("/employee/availability")
    public String employeeAvailability(Model model, Principal principal) {
        model.addAttribute("slots", service.getEmployeeSlots(principal.getName()));
        return "employee-availability";
    }

    @PostMapping("/employee/availability")
    public String addEmployeeSlot(@RequestParam String login,
                                  @RequestParam String start,
                                  @RequestParam String end) {
        service.addWindowForEmployee(login,
                LocalDateTime.parse(start),
                LocalDateTime.parse(end));
        return "redirect:/employee/availability";
    }

    // Manager pages
    @GetMapping("/manager/availability")
    public String managerAvailability(Model model, Principal principal) {
        model.addAttribute("slots", service.getManagerSlots(principal.getName()));
        return "manager-availability";
    }

    @PostMapping("/manager/availability")
    public String addManagerSlot(@RequestParam String login,
                                 @RequestParam String start,
                                 @RequestParam String end) {
        service.addWindowForManager(login,
                LocalDateTime.parse(start),
                LocalDateTime.parse(end));
        return "redirect:/manager/availability";
    }
}