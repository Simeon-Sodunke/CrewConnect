package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.ManagerRepository;
import com.example.crewconnect.Service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    private final RegistrationService reg;
    private final ManagerRepository managerRepo;

    public AdminUserController(RegistrationService reg, ManagerRepository managerRepo) {
        this.reg = reg;
        this.managerRepo = managerRepo;
    }

    /** Admin landing page: GET /admin */
    @GetMapping({"", "/"})
    public String adminHome() {
        return "admin-dashboard";
    }

    /** Create-user form */
    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("managers", managerRepo.findAll());
        return "admin-register";
    }

    /** Create user (admin-only) */
    @PostMapping("/register")
    public String submit(@RequestParam String role,
                         @RequestParam String firstname,
                         @RequestParam String lastname,
                         @RequestParam String email,
                         @RequestParam String username,
                         @RequestParam String password,
                         @RequestParam(required = false) String address,
                         @RequestParam(required = false) String phonenumber,
                         @RequestParam(required = false) Long managerId,
                         RedirectAttributes ra) {

        if ("MANAGER".equalsIgnoreCase(role)) {
            Manager m = new Manager();
            m.setFirstname(firstname);
            m.setLastname(lastname);
            m.setEmail(email);
            m.setUsername(username);
            m.setPassword(password);
            m.setAddress(address);
            m.setPhonenumber(phonenumber);
            reg.createManager(m);
        } else {
            Employee e = new Employee();
            e.setFirstname(firstname);
            e.setLastname(lastname);
            e.setEmail(email);
            e.setUsername(username);
            e.setPassword(password);
            e.setAddress(address);
            e.setPhonenumber(phonenumber);
            reg.createEmployee(e, managerId);
        }

        ra.addFlashAttribute("msg", "User created!");
        return "redirect:/admin/register";
    }
}
