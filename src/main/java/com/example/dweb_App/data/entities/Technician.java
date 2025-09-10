package com.example.dweb_App.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor  @AllArgsConstructor @Builder @ToString
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tech_seq")
    @SequenceGenerator(name = "tech_seq", sequenceName = "tech_id_seq", allocationSize = 1)

    private Long id; //I dont know if its CIN , or another id

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String cin;
    private String photoUrl; //path to the photo
    private String cnss;//il faut avoir une autre table pour ca (ou si il s'agit seulement de nom)
    @ManyToOne
    @JoinColumn(name = "car_matricule", referencedColumnName = "matricule")
    private Car car;

    @OneToMany(mappedBy = "technician")
    @JsonIgnore
    private Collection<BonIntervention> bonInterventions;

    @OneToMany(mappedBy = "technician")
    @JsonIgnore
    private  Collection<Intervention> interventions;

}
