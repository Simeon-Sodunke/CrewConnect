package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityID;

    @ManyToOne
    private Employee employee;

    @OneToMany(mappedBy = "availability")
    private List<TimeRange> window;
}