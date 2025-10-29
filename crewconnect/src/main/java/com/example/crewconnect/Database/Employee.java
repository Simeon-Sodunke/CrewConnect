package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeID;

    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String address;
    private String phonenumber;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword = true;  // default value for new rows

    @ManyToOne
    private Manager manager;

    @OneToMany(mappedBy = "employee")
    private java.util.List<Availability> availabilities;

    @OneToMany(mappedBy = "employee")
    private java.util.List<Notification> notifications;
}