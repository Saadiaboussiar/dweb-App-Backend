package com.example.dweb_App.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Entity
@AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class BonIntervention {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    private double km;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;
    private String startTime;
    private String finishTime;
    private String duration;
    private int numberIntervenant;

    @OneToOne(mappedBy = "BI")
    private Intervention intervention;

}
