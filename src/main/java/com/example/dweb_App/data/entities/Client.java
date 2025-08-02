package com.example.dweb_App.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Client {
    @Id
    private String cin;

    private String fullName;
    @Column(name = "reseauSocial")
    private String reseauSocial;
    private String contrat; //It must be another entity
    private String ville;
    private String adresse;
    @OneToMany(mappedBy = "client")
    private Collection<BonIntervention> bonInterventions;
}
