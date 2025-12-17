package com.example.dweb_App.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRentabiliteDTO {
    private String cin;                        // cin: string
    private String fullName;                   // fullName: string
    private String ville;                      // ville: string
    private int nbInterventions;               // nbInterventions: number
    private double totalKm;                    // totalKm: number
    private double coutTransport;              // coutTransport: number
    private double revenuTotal;                // revenuTotal: number
    private double tauxRentabilite;            // tauxRentabilite: number (%)

    private double rentabiliteAbsolue;         // rentabiliteAbsolue: number (DH)
    private double kmMoyenParIntervention;     // kmMoyenParIntervention: number
}
