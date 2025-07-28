package com.example.dweb_App.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class BonForm {
    private String client;
    private String ville;
    private Float km;
    private String technicianFN;
    private String technicianLN;
    private String  date;
    private String  startTme;
    private String finishTime;
    private String duration;
    private int nbreIntervenant;
    private String bonImageUrl;
}
