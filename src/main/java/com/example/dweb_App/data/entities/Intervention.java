package com.example.dweb_App.data.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Intervention {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @OneToOne
    @JoinColumn(name = "bon_intervention_id")
    private BonIntervention BI;

    private LocalDateTime submissionDate;
    private LocalDateTime validationDate;
    private int points;
}
