package com.example.dweb_App.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Car {
    @Id
    private String matricule;
    @OneToMany(mappedBy = "car")
    private Collection<Technician> technicians;
}
