package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class TimeRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeRangeID;

    private LocalDateTime start;
    private LocalDateTime end;

    @ManyToOne
    private Availability availability;
}
