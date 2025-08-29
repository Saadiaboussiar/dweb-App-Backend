package com.example.dweb_App.data.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // ← ADD CASCADE
    @JoinColumn(name = "bon_intervention_id")
    private BonIntervention BI;

    private String submissionDate;
    private String validationDate;
    private int points;
}