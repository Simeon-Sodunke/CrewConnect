package com.example.crewconnect.Service;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
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

    /* ---------- create ---------- */
    public Employee createEmployee(Employee e, Long managerId) {
        e.setPassword(encoder.encode(e.getPassword()));
        e.setMustChangePassword(true);
        if (managerId != null) mgrRepo.findById(managerId).ifPresent(e::setManager);
        return empRepo.save(e);
    }

    public Manager createManager(Manager m) {
        m.setPassword(encoder.encode(m.getPassword()));
        m.setMustChangePassword(true);
        return mgrRepo.save(m);
    }

    /* ---------- read helpers ---------- */
    public Employee getEmployee(Long id) { return empRepo.findById(id).orElseThrow(); }
    public Manager getManager(Long id) { return mgrRepo.findById(id).orElseThrow(); }

    /* ---------- uniqueness ---------- */
    public String validateUniqueness(String email, String username) {
        if (empRepo.existsByEmail(email) || mgrRepo.existsByEmail(email))
            return "Email already in use.";
        if (empRepo.existsByUsername(username) || mgrRepo.existsByUsername(username))
            return "Username already in use.";
        return null;
    }

    /** Like validateUniqueness, but ignores the current record (type/id). */
    public String validateUniquenessForUpdate(String email, String username, String type, Long id) {
        if ("EMPLOYEE".equals(type)) {
            boolean emailTaken =
                    empRepo.findByEmail(email).filter(e -> !e.getEmployeeID().equals(id)).isPresent()
                            || mgrRepo.findByEmail(email).isPresent();
            if (emailTaken) return "Email already in use.";
            boolean userTaken =
                    empRepo.findByUsername(username).filter(e -> !e.getEmployeeID().equals(id)).isPresent()
                            || mgrRepo.findByUsername(username).isPresent();
            if (userTaken) return "Username already in use.";
        } else {
            boolean emailTaken =
                    mgrRepo.findByEmail(email).filter(m -> !m.getManagerID().equals(id)).isPresent()
                            || empRepo.findByEmail(email).isPresent();
            if (emailTaken) return "Email already in use.";
            boolean userTaken =
                    mgrRepo.findByUsername(username).filter(m -> !m.getManagerID().equals(id)).isPresent()
                            || empRepo.findByUsername(username).isPresent();
            if (userTaken) return "Username already in use.";
        }
        return null;
    }

    /* ---------- update (same role) ---------- */
    public void updateEmployee(Long id,
                               String firstname,
                               String lastname,
                               String email,
                               String username,
                               String address,
                               String phonenumber,
                               String newPassword,
                               boolean mustChangePassword,
                               Long managerId) {
        Employee e = getEmployee(id);
        e.setFirstname(firstname);
        e.setLastname(lastname);
        e.setEmail(email);
        e.setUsername(username);
        e.setAddress(address);
        e.setPhonenumber(phonenumber);
        e.setMustChangePassword(mustChangePassword);

        if (newPassword != null && !newPassword.isBlank()) {
            e.setPassword(encoder.encode(newPassword));
        }
        if (managerId != null) {
            mgrRepo.findById(managerId).ifPresent(e::setManager);
        }
        empRepo.save(e);
    }

    public void updateManager(Long id,
                              String firstname,
                              String lastname,
                              String email,
                              String username,
                              String address,
                              String phonenumber,
                              String newPassword,
                              boolean mustChangePassword) {
        Manager m = getManager(id);
        m.setFirstname(firstname);
        m.setLastname(lastname);
        m.setEmail(email);
        m.setUsername(username);
        m.setAddress(address);
        m.setPhonenumber(phonenumber);
        m.setMustChangePassword(mustChangePassword);

        if (newPassword != null && !newPassword.isBlank()) {
            m.setPassword(encoder.encode(newPassword));
        }
        mgrRepo.save(m);
    }

    /* ---------- role conversions ---------- */

    /** EMPLOYEE -> MANAGER (copy fields, keep password, delete employee). */
    public void promoteEmployeeToManager(Long employeeId,
                                         String firstname,
                                         String lastname,
                                         String email,
                                         String username,
                                         String address,
                                         String phonenumber,
                                         String newPassword,
                                         boolean mustChangePassword) {

        Employee e = getEmployee(employeeId);
        // ensure target side uniqueness
        if (mgrRepo.existsByEmail(email) || mgrRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Email/username already used by a manager.");
        }

        Manager m = new Manager();
        m.setFirstname(firstname);
        m.setLastname(lastname);
        m.setEmail(email);
        m.setUsername(username);
        m.setAddress(address);
        m.setPhonenumber(phonenumber);
        m.setMustChangePassword(mustChangePassword);
        m.setPassword((newPassword != null && !newPassword.isBlank())
                ? encoder.encode(newPassword)
                : e.getPassword());

        mgrRepo.save(m);
        // NOTE: If you have employees reporting to this employee, you may want to reassign here.
        empRepo.delete(e);
    }

    /** MANAGER -> EMPLOYEE (needs optional managerId to assign). */
    public void demoteManagerToEmployee(Long managerId,
                                        String firstname,
                                        String lastname,
                                        String email,
                                        String username,
                                        String address,
                                        String phonenumber,
                                        String newPassword,
                                        boolean mustChangePassword,
                                        Long assignedManagerId) {

        Manager m = getManager(managerId);
        if (empRepo.existsByEmail(email) || empRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Email/username already used by an employee.");
        }

        Employee e = new Employee();
        e.setFirstname(firstname);
        e.setLastname(lastname);
        e.setEmail(email);
        e.setUsername(username);
        e.setAddress(address);
        e.setPhonenumber(phonenumber);
        e.setMustChangePassword(mustChangePassword);
        e.setPassword((newPassword != null && !newPassword.isBlank())
                ? encoder.encode(newPassword)
                : m.getPassword());

        if (assignedManagerId != null) {
            mgrRepo.findById(assignedManagerId).ifPresent(e::setManager);
        }
        empRepo.save(e);

        // If this manager had direct reports, you may want to reassign them before delete.
        mgrRepo.delete(m);
    }

    /* ---------- delete ---------- */
    public void deleteEmployee(Long id) { empRepo.deleteById(id); }
    public void deleteManager(Long id) { mgrRepo.deleteById(id); }
}