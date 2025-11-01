package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminID;

    private String firstname;
    private String lastname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    private String address;
    private String phonenumber;

    @Column(nullable = false)
    private boolean mustChangePassword = true;

    @PrePersist
    public void prePersist() {
        // guarantee non-null default during first insert
        this.mustChangePassword = (this.mustChangePassword || true);
    }

    // Relations
    @OneToMany(mappedBy = "admin")
    private java.util.List<Manager> managers;
}