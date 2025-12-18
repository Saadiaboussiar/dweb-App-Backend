package com.example.dweb_App.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeriodeStatsDTO {
    private String mois;                      // mois: string
    private double KmTotal;               // revenuTotal: number
    private double coutTransport;             // coutTransport: number
    private int nbInterventions;              // nbInterventions: number
}