package com.example.crewconnect.Controller;

import com.example.crewconnect.Service.PairingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@Controller
public class PairingController {

    private final PairingService pairingService;

    public PairingController(PairingService pairingService) {
        this.pairingService = pairingService;
    }

    // Displays all pairings for the currently logged-in employee
    @GetMapping("/pairings")
    public String myPairings(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("pairs", pairingService.myPairings(principal.getName()));
        } else {
            model.addAttribute("pairs", java.util.Collections.emptyList());
        }
        return "pairings";  // Thymeleaf template name
    }

    // Adds unread notification count to the model for every page using this controller
    @ModelAttribute("unreadCount")
    public long unreadCount(Principal principal) {
        if (principal == null) return 0L;
        return pairingService.unreadCount(principal.getName());
    }
}