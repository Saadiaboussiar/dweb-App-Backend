package com.example.dweb_App.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodeStatsDTO {
    private String mois;                      // mois: string
    private double revenuTotal;               // revenuTotal: number
    private double coutTransport;             // coutTransport: number
    private double rentabilite;               // rentabilite: number
    private int nbInterventions;              // nbInterventions: number
}