package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(
        name = "PAIRING",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_pair_time",
                columnNames = {"employee_a_id", "employee_b_id", "start_time", "end_time"}
        )
)
public class Pairing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_a_id", nullable = false)
    private Employee employeeA;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_b_id", nullable = false)
    private Employee employeeB;

    // Use sane column names that match the DB we want
    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @Column(nullable = false)
    private String status = "SCHEDULED";

    @Column(nullable = false)
    private String platform = "Teams";

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    private void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (platform == null || platform.isBlank()) platform = "Teams";
        if (status == null || status.isBlank()) status = "SCHEDULED";
    }
}