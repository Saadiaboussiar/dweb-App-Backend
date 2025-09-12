package com.example.dweb_App.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class UserProfileDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String cin;
    private String profileUrl;
    private String role;
    private String carMatricule;
}
