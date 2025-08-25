package com.example.dweb_App.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String phoneNumber;
    private String email;
    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private Collection<BonIntervention> bonInterventions;
}
