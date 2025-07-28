package com.example.dweb_App.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class TechnicianInfos {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String cin;
    private String cnss;

}
