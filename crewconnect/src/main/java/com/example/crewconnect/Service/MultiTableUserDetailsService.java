package com.example.crewconnect.Service;

import com.example.crewconnect.Repository.AdminRepository;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MultiTableUserDetailsService implements UserDetailsService {
    private final AdminRepository adminRepo;
    private final ManagerRepository managerRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder encoder;

    public MultiTableUserDetailsService(
            AdminRepository adminRepo,
            ManagerRepository managerRepo,
            EmployeeRepository employeeRepo,
            PasswordEncoder encoder) {
        this.adminRepo = adminRepo;
        this.managerRepo = managerRepo;
        this.employeeRepo = employeeRepo;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        return adminRepo.findByEmail(login)
                .or(() -> adminRepo.findByUsername(login))
                .map(a -> user(a.getUsername(), a.getPassword(), "ROLE_ADMIN"))
                .or(() -> managerRepo.findByEmail(login)
                        .or(() -> managerRepo.findByUsername(login))
                        .map(m -> user(m.getUsername(), m.getPassword(), "ROLE_MANAGER")))
                .or(() -> employeeRepo.findByEmail(login)
                        .or(() -> employeeRepo.findByUsername(login))
                        .map(e -> user(e.getUsername(), e.getPassword(), "ROLE_EMPLOYEE")))
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + login));
    }

    private UserDetails user(String username, String hash, String role) {
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(hash)
                .roles(role.replace("ROLE_", ""))
                .build();
    }
}