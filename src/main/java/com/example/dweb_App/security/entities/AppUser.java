package com.example.dweb_App.security.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //if there is a kind of id they work with
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "app_user_user_roles",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "app_role_id")
    )
    private Collection<AppRole> userRoles=new ArrayList<>();

}
