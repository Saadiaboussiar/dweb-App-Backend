package com.example.dweb_App.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class TechnicianCreateDTO {
    private String firstName;
    private String lastName;
    @Email(message = "Please provide a valid email address")
    private String email;
    private String phoneNumber;
    private String cin;
    private String cnss;
    private String carMatricule;
}
