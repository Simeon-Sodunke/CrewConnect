package com.example.crewconnect.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostLoginController {
    @GetMapping("/post-login")
    public String postLogin(Authentication auth) {
        var roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        if (roles.contains("ROLE_ADMIN"))   return "redirect:/admin";
        if (roles.contains("ROLE_MANAGER")) return "redirect:/manager";
        return "redirect:/employee";
    }
}