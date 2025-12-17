package com.example.dweb_App.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class DashboardMetricsDTO {
    private int points;                    // points: number
    private double bonus;                  // bonus: number
    private int interventions;             // interventions: number
    private double validationRate;         // validationRate: number
    private double successRate;            // successRate: number
    private double avgPointsPerIntervention; // avgPointsPerIntervention: number
}