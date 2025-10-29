package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityID;

    // One of these will be set (the other null):
    @ManyToOne
    private Employee employee;   // for employee-owned slots

    @ManyToOne
    private Manager manager;     // for manager-owned slots

    @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeRange> window = new ArrayList<>();
}