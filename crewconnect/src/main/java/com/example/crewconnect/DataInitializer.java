package com.example.crewconnect;

import com.example.crewconnect.Database.Admin;
import com.example.crewconnect.Repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepo, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String seedUsername = "admin";
        String seedEmail    = "admin@crewconnect.com";

        boolean exists = adminRepo.findByUsername(seedUsername).isPresent()
                || adminRepo.findByEmail(seedEmail).isPresent();

        if (!exists) {
            Admin a = new Admin();
            a.setFirstname("System");
            a.setLastname("Admin");
            a.setUsername(seedUsername);
            a.setEmail(seedEmail);
            a.setPassword(passwordEncoder.encode("Admin123"));
            a.setAddress("N/A");
            a.setPhonenumber("N/A");
            adminRepo.save(a);
            System.out.println("✅ Seeded default admin: " + seedUsername + " / Admin123");
        } else {
            System.out.println("ℹ️ Admin already present, skipping seed.");
        }
    }
}