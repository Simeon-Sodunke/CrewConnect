package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminID;
    //testing to see if my git works

    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String address;
    private String phonenumber;

    // Relations
    @OneToMany(mappedBy = "admin")
    private java.util.List<Manager> managers;
}
