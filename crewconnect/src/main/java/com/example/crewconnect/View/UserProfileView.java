package com.example.crewconnect.View;

import com.example.crewconnect.Database.Admin;
import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Manager;

/**
 * Simple view model (DTO) for rendering a user's profile page.
 * Lives in the View package because it's a UI-facing projection, not an entity or service.
 */
public class UserProfileView {

    private final Long id;
    private final String role;          // "EMPLOYEE", "MANAGER", "ADMIN"
    private final String fullName;      // "Firstname Lastname"
    private final String email;
    private final String phoneNumber;
    private final String address;
    private final String managerName;   // null for Manager/Admin
    private final boolean active;       // or compute your own "status"
    private final boolean mustChangePassword;

    private UserProfileView(Builder b) {
        this.id = b.id;
        this.role = b.role;
        this.fullName = b.fullName;
        this.email = b.email;
        this.phoneNumber = b.phoneNumber;
        this.address = b.address;
        this.managerName = b.managerName;
        this.active = b.active;
        this.mustChangePassword = b.mustChangePassword;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String role;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String address;
        private String managerName;
        private boolean active = true;
        private boolean mustChangePassword;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder managerName(String managerName) { this.managerName = managerName; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder mustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; return this; }
        public UserProfileView build() { return new UserProfileView(this); }
    }

    /* ---------- Factory helpers from your entities ---------- */

    public static UserProfileView fromEmployee(Employee e) {
        String mgrName = (e.getManager() != null)
                ? e.getManager().getFirstname() + " " + e.getManager().getLastname()
                : null;

        return UserProfileView.builder()
                .id(e.getEmployeeID())
                .role("EMPLOYEE")
                .fullName(safeJoin(e.getFirstname(), e.getLastname()))
                .email(e.getEmail())
                .phoneNumber(e.getPhonenumber())
                .address(e.getAddress())
                .managerName(mgrName)
                .mustChangePassword(e.isMustChangePassword())
                .active(true)
                .build();
    }

    public static UserProfileView fromManager(Manager m) {
        return UserProfileView.builder()
                .id(m.getManagerID())
                .role("MANAGER")
                .fullName(safeJoin(m.getFirstname(), m.getLastname()))
                .email(m.getEmail())
                .phoneNumber(m.getPhonenumber())
                .address(m.getAddress())
                .managerName(null)
                .mustChangePassword(m.isMustChangePassword())
                .active(true)
                .build();
    }

    public static UserProfileView fromAdmin(Admin a) {
        return UserProfileView.builder()
                .id(a.getAdminID())
                .role("ADMIN")
                .fullName(safeJoin(a.getFirstname(), a.getLastname()))
                .email(a.getEmail())
                .phoneNumber(a.getPhonenumber())
                .address(a.getAddress())
                .managerName(null)
                .mustChangePassword(a.isMustChangePassword())
                .active(true)
                .build();
    }

    private static String safeJoin(String first, String last) {
        String f = first == null ? "" : first.trim();
        String l = last == null ? "" : last.trim();
        return (f + " " + l).trim();
    }

    /* ---------- Getters for Thymeleaf ---------- */

    public Long getId() { return id; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getManagerName() { return managerName; }
    public boolean isActive() { return active; }
    public boolean isMustChangePassword() { return mustChangePassword; }
}