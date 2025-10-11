package com.example.crewconnect;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    //first comment in simeon branch

    @GetMapping("/")
    public String home() {
        return "index"; // loads templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin/register")
    public String adminRegister() {
        return "admin-register";
    }
}
