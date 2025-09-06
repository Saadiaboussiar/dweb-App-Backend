package com.example.dweb_App.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ChangePasswordRequest {
    @NotBlank
    @Size(min=8,message="Le mot de passe doit être au moins 8 caractères")
    private String newPassword;

    @NotBlank(message = "Mot de passe actuel est requis")
    @NotBlank
    private String currentPassword;

    @NotBlank(message = "s'il vous plait confirmer le nouveau mot de passe")
    @NotBlank
    private String confirmPassword;
}
