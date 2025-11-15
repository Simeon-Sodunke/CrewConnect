package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import com.example.crewconnect.Service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    private final RegistrationService reg;
    private final ManagerRepository managerRepo;
    private final EmployeeRepository employeeRepo;

    public AdminUserController(RegistrationService reg,
                               ManagerRepository managerRepo,
                               EmployeeRepository employeeRepo) {
        this.reg = reg;
        this.managerRepo = managerRepo;
        this.employeeRepo = employeeRepo;
    }

    @GetMapping({"", "/"})
    public String adminHome() { return "admin-dashboard"; }

    @GetMapping("/register")
    public String showForm(Model model) {
        var managers = managerRepo.findAll();
        managers.sort(Comparator
                .comparing(Manager::getFirstname, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Manager::getLastname, String.CASE_INSENSITIVE_ORDER));
        model.addAttribute("managers", managers);
        return "admin-register";
    }

    @PostMapping("/register")
    public String submit(@RequestParam("role") String role,
                         @RequestParam("firstname") String firstname,
                         @RequestParam("lastname") String lastname,
                         @RequestParam("email") String email,
                         @RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam(value = "address", required = false) String address,
                         @RequestParam(value = "phonenumber", required = false) String phonenumber,
                         @RequestParam(value = "managerId", required = false) Long managerId,
                         Model model) {

        // Always re-add managers so the form can render correctly
        model.addAttribute("managers", managerRepo.findAll());

        // Uniqueness gate
        String err = reg.validateUniqueness(email, username);
        if (err != null) {
            model.addAttribute("error", err);
            return "admin-register";
        }

        try {
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
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "admin-register";
        }

        model.addAttribute("msg", "User created!");
        return "admin-register";
    }
    /** Users table with optional search & role filter */
    @GetMapping("/users")
    public String users(@RequestParam(value = "q", required = false) String q,
                        @RequestParam(value = "role", required = false, defaultValue = "ALL") String role,
                        Model model) {

        String query = (q == null) ? "" : q.trim().toLowerCase();
        String filter = role == null ? "ALL" : role.trim().toUpperCase();

        List<UserRowView> rows = new ArrayList<>();

        if ("EMPLOYEE".equals(filter) || "ALL".equals(filter)) {
            for (Employee e : employeeRepo.findAll()) {
                if (matches(e.getFirstname(), e.getLastname(), e.getEmail(), e.getUsername(), query)) {
                    rows.add(UserRowView.fromEmployee(e));
                }
            }
        }
        if ("MANAGER".equals(filter) || "ALL".equals(filter)) {
            for (Manager m : managerRepo.findAll()) {
                if (matches(m.getFirstname(), m.getLastname(), m.getEmail(), m.getUsername(), query)) {
                    rows.add(UserRowView.fromManager(m));
                }
            }
        }

        rows.sort(Comparator.comparing(UserRowView::getName, String.CASE_INSENSITIVE_ORDER));

        model.addAttribute("rows", rows);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("count", rows.size());
        model.addAttribute("role", filter);
        return "admin-users";
    }

    /* ------------------- EDIT: load form ------------------- */
    @GetMapping("/users/{type}/{id}/edit")
    public String editUser(@PathVariable("type") String type,
                           @PathVariable("id") Long id,
                           Model model) {

        String t = normalize(type);
        model.addAttribute("type", t);

        if ("EMPLOYEE".equals(t)) {
            Employee e = employeeRepo.findById(id).orElseThrow();
            model.addAttribute("user", e);
            model.addAttribute("targetId", e.getEmployeeID());
        } else if ("MANAGER".equals(t)) {
            Manager m = managerRepo.findById(id).orElseThrow();
            model.addAttribute("user", m);
            model.addAttribute("targetId", m.getManagerID());
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }

        model.addAttribute("managers", managerRepo.findAll());
        return "admin-user-edit";
    }

    /* ------------------- EDIT: submit (update/convert) ------------------- */
    @PostMapping("/users/{type}/{id}/edit")
    public String updateUser(@PathVariable("type") String type,
                             @PathVariable("id") Long id,
                             @RequestParam("firstname") String firstname,
                             @RequestParam("lastname") String lastname,
                             @RequestParam("email") String email,
                             @RequestParam("username") String username,
                             @RequestParam(value = "address", required = false) String address,
                             @RequestParam(value = "phonenumber", required = false) String phonenumber,
                             @RequestParam(value = "password", required = false) String password, // optional new password
                             @RequestParam(value = "managerId", required = false) Long managerId,  // for employees / demotions
                             @RequestParam(value = "mustChangePassword",
                                     required = false,
                                     defaultValue = "false") boolean mustChangePassword,
                             @RequestParam(name = "newRole") String newRole,
                             Model model) {

        String current = normalize(type);
        String target = normalize(newRole);

        // Uniqueness validation for update in current role
        String err = reg.validateUniquenessForUpdate(email, username, current, id);
        if (err != null) {
            model.addAttribute("error", err);
            return editUser(current, id, model);
        }

        if (!target.equals(current)) {
            // Role change requested
            if ("MANAGER".equals(target) && "EMPLOYEE".equals(current)) {
                reg.promoteEmployeeToManager(id, firstname, lastname, email, username,
                        address, phonenumber, password, mustChangePassword);
                return "redirect:/admin/users?role=MANAGER";
            } else if ("EMPLOYEE".equals(target) && "MANAGER".equals(current)) {
                reg.demoteManagerToEmployee(id, firstname, lastname, email, username,
                        address, phonenumber, password, mustChangePassword, managerId);
                return "redirect:/admin/users?role=EMPLOYEE";
            } else {
                throw new IllegalArgumentException("Unsupported role change: " + current + " -> " + target);
            }
        }

        // Same-role update
        if ("EMPLOYEE".equals(current)) {
            reg.updateEmployee(id, firstname, lastname, email, username, address, phonenumber,
                    password, mustChangePassword, managerId);
            return "redirect:/admin/users?role=EMPLOYEE";
        } else {
            reg.updateManager(id, firstname, lastname, email, username, address, phonenumber,
                    password, mustChangePassword);
            return "redirect:/admin/users?role=MANAGER";
        }
    }

    /* ------------------- DELETE ------------------- */
    @PostMapping("/users/{type}/{id}/delete")
    public String deleteUser(@PathVariable("type") String type,
                             @PathVariable("id") Long id) {
        String t = normalize(type);
        if ("EMPLOYEE".equals(t)) {
            reg.deleteEmployee(id);
        } else if ("MANAGER".equals(t)) {
            reg.deleteManager(id);
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
        return "redirect:/admin/users?role=" + t;
    }

    /* --------------- helpers --------------- */
    private static String normalize(String t) {
        return t == null ? "" : t.trim().toUpperCase();
    }

    private boolean matches(String first, String last, String email, String user, String q) {
        if (q.isEmpty()) return true;
        String full = ((first == null ? "" : first) + " " + (last == null ? "" : last)).toLowerCase();
        return (full.contains(q))
                || ((email == null ? "" : email).toLowerCase().contains(q))
                || ((user == null ? "" : user).toLowerCase().contains(q));
    }

    /** Table DTO includes id + type so we can build Edit/Delete links */
    public static class UserRowView {
        private final Long id;
        private final String type; // EMPLOYEE | MANAGER
        private final String name;
        private final String email;
        private final String role;
        private final String status;

        private UserRowView(Long id, String type, String name, String email, String role, String status) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.email = email;
            this.role = role;
            this.status = status;
        }

        public static UserRowView fromEmployee(Employee e) {
            String name = safeJoin(e.getFirstname(), e.getLastname());
            String status = e.isMustChangePassword() ? "Must Reset Password" : "Active";
            return new UserRowView(e.getEmployeeID(), "EMPLOYEE", name, e.getEmail(), "EMPLOYEE", status);
        }

        public static UserRowView fromManager(Manager m) {
            String name = safeJoin(m.getFirstname(), m.getLastname());
            String status = m.isMustChangePassword() ? "Must Reset Password" : "Active";
            return new UserRowView(m.getManagerID(), "MANAGER", name, m.getEmail(), "MANAGER", status);
        }

        private static String safeJoin(String f, String l) {
            String a = f == null ? "" : f.trim();
            String b = l == null ? "" : l.trim();
            return (a + " " + b).trim();
        }

        public Long getId() { return id; }
        public String getType() { return type; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
    }
}
