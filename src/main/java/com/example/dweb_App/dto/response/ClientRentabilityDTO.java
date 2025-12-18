package com.example.dweb_App.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRentabilityDTO {
    private String cin;                        // cin: string
    private String fullName;                   // fullName: string
    private String ville;                      // ville: string
    private int nbInterventions;               // nbInterventions: number
    private double totalKm;                    // totalKm: number
    private double coutTransport;        // rentabiliteAbsolue: number (DH)
    private double kmMoyenParIntervention;
    private double coutParIntervention;
}
