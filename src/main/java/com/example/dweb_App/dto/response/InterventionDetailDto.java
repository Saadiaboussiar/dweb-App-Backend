package com.example.dweb_App.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterventionDetailDto {
    private Long id;
    private String date;
    private Double distanceKm;
    private String technicianName;
    private String status;
}
