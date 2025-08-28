package com.example.dweb_App.web;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.service.InterventionService;
import com.example.dweb_App.dto.response.InterventionDetailsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/intervention")
public class InterventionController {
    private InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }


    @GetMapping("/interventionsDetails")
    public ResponseEntity<?> getInterventionsDetails(){

        List<Intervention> interventions=interventionService.getAllInterventions();
        if(!interventions.isEmpty()){
            List<InterventionDetailsDTO> interventionDetailsDTOList=new ArrayList<>();

            for(Intervention intervention:interventions ){

                Technician technician=intervention.getTechnician();
                String firstName= technician.getFirstName();
                String lastName= technician.getLastName();
                String technicianFullName=firstName+lastName;

                BonIntervention bonIntervention=intervention.getBI();
                Client client=bonIntervention.getClient();
                String clientFullName= client.getFullName();

                String ville=bonIntervention.getVille();
                String dateAndTime=intervention.getSubmissionDate();
                String[] parts=dateAndTime.split(" ");
                String rowDate=parts[0];
                String rawTime=parts[0];

                InterventionDetailsDTO interventionDetails= InterventionDetailsDTO.builder()
                        .interId(intervention.getId())
                        .technicianFullName(technicianFullName)
                        .client(clientFullName)
                        .ville(ville)
                        .date(rowDate)
                        .submittedAt(rawTime)
                        .build();

                interventionDetailsDTOList.add(interventionDetails);
            }

            return ResponseEntity.ok(interventionDetailsDTOList);
        }
        return ResponseEntity.noContent().build();
    }
}
