package com.example.dweb_App.web;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.service.BonInterventionService;
import com.example.dweb_App.data.service.InterventionService;
import com.example.dweb_App.dto.response.InterventionDetailsDTO;
import com.example.dweb_App.dto.response.InterventionInfosDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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



    @GetMapping
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
                        .nbreIntervenant(intervention.getBI().getNumberIntervenant()).build();

                interventionDetailsDTOList.add(interventionDetailsDTO);
            }
            return ResponseEntity.ok(interventionDetailsDTOList);
        }
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/interventionsDetails")
    public ResponseEntity<?> getInterventionsDetails(){

        List<Intervention> interventions=interventionService.getAllInterventions();
        if(!interventions.isEmpty()){
            List<InterventionInfosDTO> interventionDetailsDTOList=new ArrayList<>();

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
                String rawTime=parts[1];

                InterventionInfosDTO interventionDetails= InterventionInfosDTO.builder()
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

    @DeleteMapping("/{interventionId}")
    public ResponseEntity<?> deleteIntervention(@PathVariable Long interventionId){

        Intervention intervention=interventionService.findInterventionById(interventionId)
                .orElseThrow(()->new EntityNotFoundException("intervention not Found "+interventionId));

        if(intervention.getBI()!=null){
            intervention.getBI().setIntervention(null);
            intervention.setBI(null);
        }
        if(intervention.getTechnician()!=null){
            intervention.getTechnician().getInterventions().remove(intervention);
        }
        interventionService.deleteIntervention(interventionId);
        bonInterventionService.deleteBonIntervention(intervention.getBI().getId());

        return ResponseEntity.ok(Map.of(
                "message", "Intervention et son bon sont bien supprimées ",
                "interventionId", intervention.getId()));
    }



}
