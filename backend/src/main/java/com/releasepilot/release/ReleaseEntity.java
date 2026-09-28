package com.releasepilot.release;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "releases")
public class ReleaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReleaseStatus status;

    protected ReleaseEntity() {
    }

    public ReleaseEntity(String name, LocalDate targetDate, ReleaseStatus status) {
        this.name = name;
        this.targetDate = targetDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getTargetDate() { return targetDate; }
    public ReleaseStatus getStatus() { return status; }
}
