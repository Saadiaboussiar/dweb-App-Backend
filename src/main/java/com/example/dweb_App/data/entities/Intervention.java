package com.example.dweb_App.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

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

    @Enumerated(EnumType.STRING)
    private InterventionStatus status=InterventionStatus.PENDING;

    private String actionDateTime;

    private int points;

    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL)
    private Collection<Notifications> notifications;


}

