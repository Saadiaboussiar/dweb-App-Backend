package com.example.dweb_App.dto.response;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TechnicianBonusDTO {

    private Long id;
    private String name;
    private String email;
    private int currentPoints;
    private double totalRewards;

    private String cin;
    private String phone;

    private Integer totalInterventions;
    private double totalHours;
    private Double totalDistance;
}
