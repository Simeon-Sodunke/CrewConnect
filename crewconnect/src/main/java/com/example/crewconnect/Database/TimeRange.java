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

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "availability_id", nullable = false)
    private Availability availability;

    @PrePersist @PreUpdate
    private void validate() {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("TimeRange: end must be after start.");
        }
    }
}