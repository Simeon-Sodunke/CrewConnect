package com.example.crewconnect.Service;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.AdminRepository;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import jakarta.annotation.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final PasswordEncoder encoder;
    private final EmployeeRepository empRepo;
    private final ManagerRepository mgrRepo;

    public RegistrationService(PasswordEncoder encoder,
                               EmployeeRepository empRepo,
                               ManagerRepository mgrRepo) {
        this.encoder = encoder;
        this.empRepo = empRepo;
        this.mgrRepo = mgrRepo;
    }

    public Employee createEmployee(Employee e, Long managerId) {
        e.setPassword(encoder.encode(e.getPassword()));
        e.setMustChangePassword(true);
        if (managerId != null) {
            mgrRepo.findById(managerId).ifPresent(e::setManager);
        }
        return empRepo.save(e);
    }

    public Manager createManager(Manager m) {
        m.setPassword(encoder.encode(m.getPassword()));   // <— encode
        m.setMustChangePassword(true);                    // <— force first-login change
        return mgrRepo.save(m);
    }
}