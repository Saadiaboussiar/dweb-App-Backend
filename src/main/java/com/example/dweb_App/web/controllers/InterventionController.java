package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.*;
import com.example.dweb_App.data.service.BonInterventionService;
import com.example.dweb_App.data.service.InterventionService;
import com.example.dweb_App.dto.response.InterventionDetailsDTO;
import com.example.dweb_App.dto.response.InterventionEssentialsDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/intervention")

public class InterventionController {
    private InterventionService interventionService;
    private BonInterventionService bonInterventionService;

    public InterventionController(InterventionService interventionService, BonInterventionService bonInterventionService) {
        this.interventionService = interventionService;
        this.bonInterventionService = bonInterventionService;
    }



    @GetMapping("/interventionsDetails")
    public ResponseEntity<?> getAllIntervention(){

        List<Intervention> interventions=interventionService.getAllInterventions();

        if(!interventions.isEmpty()) {
            List<InterventionDetailsDTO> interventionDetailsDTOList = new ArrayList<>();

            for(Intervention intervention:interventions){
                InterventionDetailsDTO interventionDetailsDTO= InterventionDetailsDTO.builder()
                        .client(intervention.getBI().getClient().getFullName())
                        .interId(intervention.getId())
                        .technicianFN(intervention.getTechnician().getFirstName())
                        .technicianLN(intervention.getTechnician().getLastName())
                        .km(intervention.getBI().getKm())
                        .date(intervention.getBI().getDate())
                        .ville(intervention.getBI().getVille())
                        .startTime(intervention.getBI().getStartTime())
                        .finishTime(intervention.getBI().getFinishTime())
                        .duration(intervention.getBI().getDuration())
                        .submittedAt(intervention.getSubmissionDate())
                        .interUrl(intervention.getBI().getBonImageUrl())
                        .nbreIntervenant(intervention.getBI().getNumberIntervenant())
                        .build();

                interventionDetailsDTOList.add(interventionDetailsDTO);
            }
            return ResponseEntity.ok(interventionDetailsDTOList);
        }
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/interventionsEssentials")
    public ResponseEntity<?> getInterventionsDetails(){

        List<Intervention> interventions=interventionService.getAllInterventions();
        if(!interventions.isEmpty()){
            List<InterventionEssentialsDTO> interventionDetailsDTOList=new ArrayList<>();

            for(Intervention intervention:interventions ){

                Technician technician=intervention.getTechnician();
                String firstName= technician.getFirstName();
                String lastName= technician.getLastName();
                String technicianFullName=firstName+" "+lastName;

                BonIntervention bonIntervention=intervention.getBI();
                Client client=bonIntervention.getClient();
                String clientFullName= client.getFullName();

                String ville=bonIntervention.getVille();
                String dateAndTime=intervention.getSubmissionDate();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);

                LocalDateTime dateTime = LocalDateTime.parse(dateAndTime, formatter);

                LocalDate date = dateTime.toLocalDate();
                LocalTime time = dateTime.toLocalTime();

                InterventionEssentialsDTO interventionEssentials= InterventionEssentialsDTO.builder()
                        .interId(intervention.getId())
                        .technicianFullName(technicianFullName)
                        .client(clientFullName)
                        .ville(ville)
                        .date(date.toString())
                        .submittedAt(time.toString())
                        .status(intervention.getStatus())
                        .build();

                interventionDetailsDTOList.add(interventionEssentials);
            }

            return ResponseEntity.ok(interventionDetailsDTOList);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{interventionId}")
    public ResponseEntity<?> deleteIntervention(@PathVariable Long interventionId){

        interventionService.deleteIntervention(interventionId);

        return ResponseEntity.ok(Map.of(
                "message", "Intervention et son bon sont bien supprimées ",
                "interventionId", interventionId));
    }

    @PutMapping("/{interId}")
    public ResponseEntity<?> validateIntervention(@PathVariable Long interId, @RequestParam Boolean isValidate){

        try{
            interventionService.updateInterventionStatus(interId,isValidate);
            return ResponseEntity.ok().build();

        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();

        }
    }

    @GetMapping("/status/{interId}")
    public ResponseEntity<?> getInterventionPointsCategory(@PathVariable Long interId){

        Intervention intervention=interventionService.findInterventionById(interId)
                .orElseThrow(()->new EntityNotFoundException("Intervention Not Found"));

        int interventionPoints=intervention.getPoints();
        return ResponseEntity.ok(interventionPoints);


    }


}
