package com.example.dweb_App.dto.response;

import com.example.dweb_App.dto.request.TechnicianCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class TechnicianResponseDTO {
    private Long id;
    private String profileUrl;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String cin;
    private String cnss;
}
