package com.example.dweb_App.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class UserCreateDTO {
    private String username;
    @Email(message = "Entrez une dresse e-mail Valide")
    private String email;
    private String password;
}
