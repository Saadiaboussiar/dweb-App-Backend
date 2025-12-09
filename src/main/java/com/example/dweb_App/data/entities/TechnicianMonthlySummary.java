package com.example.dweb_App.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor @Data @AllArgsConstructor @Builder

public class TechnicianMonthlySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_technician_monthly_summary_technician"))
    private Technician technician;

    @Column(name = "month_year", nullable = false)
    private LocalDate monthYear; // Store as first day of the month

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "total_bonus", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalBonus = BigDecimal.ZERO;

    @Column(name = "interventions_count", nullable = false)
    private Integer interventionsCount = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

}
