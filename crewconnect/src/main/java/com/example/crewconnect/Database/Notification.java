package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Employee employee;

    @Column(nullable = false, length = 300)
    private String message;

    @Column(nullable = false)
    private String type = "PAIRING"; // you can extend later

    @Column(nullable = false)
    private boolean readFlag = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}