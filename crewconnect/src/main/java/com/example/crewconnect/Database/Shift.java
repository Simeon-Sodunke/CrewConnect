package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftID;

    private LocalDateTime start;
    private LocalDateTime end;

    @ManyToOne
    private Schedule schedule;
}
