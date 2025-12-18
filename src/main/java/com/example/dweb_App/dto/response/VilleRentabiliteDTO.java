package com.example.dweb_App.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class VilleRentabiliteDTO {
    private String name;                      // name: string
    private int nbClients;                    // nbClients: number
    private int nbInterventions;              // nbInterventions: number
    private double totalKm;
}
