package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleID;

    @ManyToOne
    private Employee user;

    @OneToMany(mappedBy = "schedule")
    private List<Shift> entry;
}