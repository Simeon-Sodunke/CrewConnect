package com.example.crewconnect.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PostLoginController {

    @GetMapping("/post-login")
    public String postLogin(Authentication auth) {
        // Make it an explicit List<String> to avoid inference issues
        List<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()); // use Collectors for widest compatibility

        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/admin";
        }
        if (roles.contains("ROLE_MANAGER")) {
            return "redirect:/manager";
        }
        return "redirect:/employee";
    }
}