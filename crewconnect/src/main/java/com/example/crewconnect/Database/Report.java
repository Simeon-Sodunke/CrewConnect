package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportID;

    private String title;
    private LocalDateTime timeGenerated;

    @ManyToOne
    private Manager manager;
}