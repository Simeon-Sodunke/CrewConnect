package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String address;
    private String phonenumber;

    @ManyToOne
    private Admin admin;

    @OneToMany(mappedBy = "manager")
    private java.util.List<Employee> employees;

    @OneToMany(mappedBy = "manager")
    private java.util.List<Report> reports;
}
