package com.example.crewconnect.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("")
    public String managerDashboard(Model model) {
        model.addAttribute("pageTitle", "Manager Dashboard");
        return "manager-dashboard";   // looks for templates/manager-dashboard.html
    }
}