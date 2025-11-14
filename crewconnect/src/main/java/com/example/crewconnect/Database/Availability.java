package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityID;

    // Exactly one of these will be set
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Manager manager;

    @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<TimeRange> window = new ArrayList<>();

    /** Helper to keep both sides in sync */
    public void addRange(TimeRange tr) {
        tr.setAvailability(this);
        window.add(tr);
    }

    public void removeRange(TimeRange tr) {
        tr.setAvailability(null);
        window.remove(tr);
    }

    @PrePersist @PreUpdate
    private void validateOwner() {
        boolean hasEmployee = employee != null;
        boolean hasManager  = manager  != null;
        if (hasEmployee == hasManager) {
            throw new IllegalStateException("Availability must belong to exactly one owner: employee OR manager.");
        }
    }
}