package com.example.dweb_App.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor  @AllArgsConstructor @Builder @ToString
public class Technician {
    @Id
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
    private Collection<BonIntervention> bonInterventions;

    @OneToMany(mappedBy = "technician")
    private  Collection<Intervention> interventions;

}
