package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long managerID;

    private String firstname;
    private String lastname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    private String address;
    private String phonenumber;

    private boolean mustChangePassword = true;

    @ManyToOne
    private Admin admin;

    @OneToMany(mappedBy = "manager")
    private java.util.List<Employee> employees;

    @OneToMany(mappedBy = "manager")
    private java.util.List<Report> reports;
}
