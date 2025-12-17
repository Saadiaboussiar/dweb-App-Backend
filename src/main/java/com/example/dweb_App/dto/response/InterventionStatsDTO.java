package com.example.dweb_App.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class InterventionStatsDTO {
    private int total;
    private int validated;
    private int rejected;
    private int pending;
    private double validationRate;
}
