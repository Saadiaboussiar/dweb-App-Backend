package com.example.dweb_App.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class InterventionInfosDTO {
    private String technicianFullName;
    private String client;
    private String ville;
    private  String submittedAt;
    private String date;
    private Long interId;
}
