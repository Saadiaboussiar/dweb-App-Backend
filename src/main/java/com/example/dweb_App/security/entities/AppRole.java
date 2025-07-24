package com.example.dweb_App.security.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class AppRole {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roleName;

    @ManyToMany(mappedBy = "userRoles")
    private Collection<AppUser> users= new ArrayList<>();
}
