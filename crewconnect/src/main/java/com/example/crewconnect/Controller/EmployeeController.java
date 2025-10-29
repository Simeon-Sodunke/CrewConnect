package com.example.crewconnect.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    // GET /employee  → Employee dashboard
    @GetMapping("")
    public String employeeDashboard(Model model) {
        model.addAttribute("pageTitle", "Employee Dashboard");
        return "employee-dashboard"; // looks for templates/employee-dashboard.html
    }

}