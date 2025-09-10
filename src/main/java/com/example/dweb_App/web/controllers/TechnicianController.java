package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.service.TechnicianService;
import com.example.dweb_App.dto.request.TechnicianCreateDTO;
import com.example.dweb_App.dto.response.InterventionDetailsDTO;
import com.example.dweb_App.dto.response.TechnicianResponseDTO;
import com.example.dweb_App.dto.response.UserProfileDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.exception.UpdateFailedException;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import com.example.dweb_App.utils.PasswordService;
import com.example.dweb_App.utils.resend.ResendEmailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/technicianInfos")

public class TechnicianController {
    private TechnicianService technicianService;
    private AppService appService;
    private ResendEmailService emailService;
    private PasswordService passwordService;

    public TechnicianController(TechnicianService technicianService, AppService appService, ResendEmailService emailService, PasswordService passwordService) {
        this.technicianService = technicianService;
        this.appService = appService;
        this.emailService = emailService;
        this.passwordService = passwordService;
    }

    @PostAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TechnicianResponseDTO>> getAllTechnicians(){

       List<Technician> technicians=technicianService.allTechnicians();
       if(!technicians.isEmpty()){
           List<TechnicianResponseDTO> technicianResponseList=new ArrayList<>();
           for(Technician technician:technicians){
               TechnicianResponseDTO technicianResponse= TechnicianResponseDTO.builder()
                       .id(technician.getId())
                       .firstName(technician.getFirstName())
                       .lastName(technician.getLastName())
                       .email(technician.getEmail())
                       .phoneNumber(technician.getPhoneNumber())
                       .cin(technician.getCin())
                       .cnss(technician.getCnss())
                       .profileUrl(technician.getPhotoUrl()).build();
               technicianResponseList.add(technicianResponse);
           }
            return ResponseEntity.ok(technicianResponseList);
        }
       return ResponseEntity.noContent().build();

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addNewTechnician(@RequestBody @Valid TechnicianCreateDTO technician){

        Technician newTechnician= Technician.builder().cin(technician.getCin()).email(technician.getEmail()).cnss(technician.getCnss()).firstName(technician.getFirstName()).lastName(technician.getLastName()).phoneNumber(technician.getPhoneNumber()).build();
        Technician addedTechnician=technicianService.addNewTechnician(newTechnician);

        try {

            String username=addedTechnician.getFirstName() + ' ' + addedTechnician.getLastName();
            String email= addedTechnician.getEmail();
            String tempPassword= PasswordService.generatePassword();

            AppUser newUser = AppUser.builder()
                    .email(email)
                    .username(username)
                    .password(tempPassword)
                    .userRoles(new ArrayList<>())
                    .passwordChangeRequired(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            appService.addNewUser(newUser);

            emailService.sendWelcomeEmail(email, username,tempPassword);

            return ResponseEntity.ok(addedTechnician.getId());

        }catch(Exception e){
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }


    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTechnician(@PathVariable Long id, @RequestBody @Valid TechnicianCreateDTO technician){

        try{
            Technician existingTechnician=technicianService.loadTechnicianById(id)
                    .orElseThrow(()->new EntityNotFoundException("Technician not Found "+id));

            existingTechnician.setCnss(technician.getCnss());
            existingTechnician.setCin(technician.getCin());
            existingTechnician.setEmail(technician.getEmail());
            existingTechnician.setLastName(technician.getLastName());
            existingTechnician.setFirstName(technician.getFirstName());
            existingTechnician.setPhoneNumber(technician.getPhoneNumber());

            Technician updatedTechnician=technicianService.saveTechnician(existingTechnician);

            return ResponseEntity.ok(updatedTechnician);
        }catch (DataIntegrityViolationException e){
            throw new UpdateFailedException("Update would violate database constraints: "+e.getMessage(),e);
        }catch (Exception e){
            throw new UpdateFailedException("Failed to update client: "+e.getMessage(),e);
        }

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTechnician(@PathVariable Long id){
        Technician technician=technicianService.loadTechnicianById(id)
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+id));

        technicianService.deleteTechnicianById(id);
        String username=technician.getFirstName()+" "+technician.getLastName();
        AppUser user=appService.loadUserByUsername(username)
                .orElseThrow(()->new EntityNotFoundException("User not Found "+username));
        appService.deleteUserById(user.getId());

        return ResponseEntity.ok(Map.of(
                "message: ", "Technician deleted successfully",
                "technician name: ", username
        ));
    }

    @GetMapping("/{technicianEmail}")
    public ResponseEntity<?> getTechnicianInterventions(@PathVariable String technicianEmail) {

        Technician technician = technicianService.loadTechnicianByEmail(technicianEmail)
                .orElseThrow(() -> new EntityNotFoundException("Technician Not Found " + technicianEmail));
        List<Intervention> interventions = technician.getInterventions().stream().toList();

        if (!interventions.isEmpty()) {
            List<InterventionDetailsDTO> interventionDetailsDTOList = new ArrayList<>();

            for (Intervention intervention : interventions) {
                InterventionDetailsDTO interventionDetailsDTO = InterventionDetailsDTO.builder()
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




}
