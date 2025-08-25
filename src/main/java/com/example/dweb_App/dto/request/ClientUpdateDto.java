package com.example.dweb_App.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class ClientUpdateDto {
    private String cin;
    private String fullName;
    private String reseauSocial;
    private String contrat; //It must be another entity
    private String ville;
    private String adresse;
    private String phoneNumber;
    @Email(message="Please provide a valid email address")
    private String email;
}
