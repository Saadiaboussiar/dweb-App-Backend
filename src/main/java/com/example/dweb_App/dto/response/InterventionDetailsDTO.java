package com.example.dweb_App.dto.response;

import com.example.dweb_App.data.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class InterventionDetailsDTO {
    private Long interId;
    private String client;
    private String ville;
    private double km;
    private String date;
    private String technicianFN;
    private String technicianLN;
    private String startTime;
    private String finishTime;
    private String duration;
    private int nbreIntervenant;
    private String interUrl;
    private String submittedAt;
    
}
