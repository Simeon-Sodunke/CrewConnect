package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class TimeRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeRangeId;

    @Column(name = "start_time")
    private LocalDateTime start;

    @Column(name = "end_time")
    private LocalDateTime end;

    @ManyToOne
    private Availability availability;
}
