package com.example.dweb_App.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor @Builder @ToString
@Table(name = "bonIntervention")
public class BonIntervention {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "client_cin")
    private Client client;
    private String ville;
    private double km;
    private String date;
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;
    private String startTime;
    private String finishTime;
    private String duration;
    private int numberIntervenant;
    private String bonImageUrl;

    @OneToOne(mappedBy = "BI")
    private Intervention intervention;

}
