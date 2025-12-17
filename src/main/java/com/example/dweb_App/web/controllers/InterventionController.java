package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.*;
import com.example.dweb_App.data.service.BonInterventionService;
import com.example.dweb_App.data.service.ClientService;
import com.example.dweb_App.data.service.InterventionService;
import com.example.dweb_App.data.service.TechnicianService;
import com.example.dweb_App.dto.response.InterventionDetailsDTO;
import com.example.dweb_App.dto.response.InterventionEssentialsDTO;
import com.example.dweb_App.dto.response.InterventionStatsDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.dweb_App.data.entities.InterventionStatus.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/intervention")

public class InterventionController {
    private InterventionService interventionService;
    private BonInterventionService bonInterventionService;
    private TechnicianService technicianService;
    private ClientService clientService;

    public InterventionController(InterventionService interventionService, BonInterventionService bonInterventionService, TechnicianService technicianService, ClientService clientService) {
        this.interventionService = interventionService;
        this.bonInterventionService = bonInterventionService;
        this.technicianService = technicianService;
        this.clientService = clientService;
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
                        .status(intervention.getStatus())
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
                        .updated(intervention.isUpdated())
                        .updateDateTime(intervention.getUpdateDateTime())
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
        Intervention intervention= interventionService.findInterventionById(interId)
                .orElseThrow(()->new EntityNotFoundException("Intervention Not Found "+interId));

        Technician technician=intervention.getTechnician();

        try{

            interventionService.updateInterventionStatus(interId,isValidate,technician.getId());
            return ResponseEntity.ok().build();

        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();

        }
    }

    @GetMapping("/status/{interId}")
    public ResponseEntity<?> getInterventionPointsCategory(@PathVariable Long interId){

        Intervention intervention=interventionService.findInterventionById(interId)
                .orElseThrow(()->new EntityNotFoundException("Intervention Not Found"));

        BigDecimal interventionPoints=intervention.getBonusAmount();
        return ResponseEntity.ok(interventionPoints);


    }


    @PutMapping("/editIntervention/{interventionId}")
    public ResponseEntity<?> editIntervention(@PathVariable Long interventionId, @RequestBody InterventionDetailsDTO interventionDetails){

       Intervention intervention= interventionService.findInterventionById(interventionId)
               .orElseThrow(()->new EntityNotFoundException("Intervention Not Found "+interventionId));

       BonIntervention bonIntervention=intervention.getBI();
       Client client=clientService.loadClient(interventionDetails.getClient())
               .orElseThrow(()->new EntityNotFoundException("Client Not Found "+interventionDetails.getClient()));

       Technician technician=technicianService.loadTechnician(interventionDetails.getTechnicianFN(),interventionDetails.getTechnicianLN())
               .orElseThrow(()->new EntityNotFoundException("Client Not Found "+interventionDetails.getTechnicianFN()+interventionDetails.getTechnicianLN()));

       LocalDateTime now = LocalDateTime.now();

       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
       String frenchDateTime = now.format(formatter);

        bonIntervention.setClient(client);
        bonIntervention.setTechnician(technician);
        bonIntervention.setKm(interventionDetails.getKm());
        bonIntervention.setDate(interventionDetails.getDate());
        bonIntervention.setDuration(interventionDetails.getDuration());
        bonIntervention.setFinishTime(interventionDetails.getFinishTime());
        bonIntervention.setNumberIntervenant(interventionDetails.getNbreIntervenant());
        bonIntervention.setStartTime(interventionDetails.getStartTime());
        bonIntervention.setVille(interventionDetails.getVille());

        intervention.setStatus(PENDING);
        intervention.setSubmissionDate(interventionDetails.getSubmittedAt());
        intervention.setUpdated(true);
        intervention.setUpdateDateTime(frenchDateTime);

        bonInterventionService.addNewBonIntervention(bonIntervention);
        interventionService.addNewIntervention(intervention);

        return ResponseEntity.ok("Intervention was updated successfully");

    }


    @GetMapping("/statistics/{techId}")
    public ResponseEntity<InterventionStatsDTO> getTechnicianStatistics(@PathVariable Long techId){

        Technician technician=technicianService.loadTechnicianById(techId)
                .orElseThrow(()->new EntityNotFoundException("Client Not Found "+techId));

        List<Intervention> interventions=technician.getInterventions().stream().toList();
        int totalInterventions=interventions.size();

        Map<InterventionStatus, Long> statusCounts = interventions.stream()
                .collect(Collectors.groupingBy(
                        Intervention::getStatus,
                        Collectors.counting()
                ));

        int validated = statusCounts.getOrDefault(VALIDATED, 0L).intValue();
        int rejected = statusCounts.getOrDefault(REJECTED, 0L).intValue();
        int pending = statusCounts.getOrDefault(PENDING, 0L).intValue();
        double validationRate = ((double) validated / (validated + rejected)) * 100;

        InterventionStatsDTO interventionStatsDTO= InterventionStatsDTO.builder()
                .total(totalInterventions)
                .validated(validated)
                .rejected(rejected)
                .pending(pending)
                .validationRate(validationRate).build();

        return ResponseEntity.ok(interventionStatsDTO);
    }


}
