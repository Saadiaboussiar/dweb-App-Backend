package com.example.dweb_App.dto.response;

import com.example.dweb_App.data.entities.InterventionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class InterventionEssentialsDTO {
    private String technicianFullName;
    private String client;
    private String ville;
    private  String submittedAt;
    private String date;
    private Long interId;
    private InterventionStatus status;
    private boolean updated ;
    private String updateDateTime;

}
