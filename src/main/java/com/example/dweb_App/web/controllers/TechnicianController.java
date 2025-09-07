package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.service.TechnicianService;
import com.example.dweb_App.dto.request.TechnicianCreateDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    public ResponseEntity<List<Technician>> getAllTechnicians(){

       List<Technician> technicians=technicianService.allTechnicians();
       if(!technicians.isEmpty()){
            return ResponseEntity.ok(technicians);
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
        return ResponseEntity.ok(Map.of(
                "message", "Client deleted successfully",
                "clientName", technician.getFirstName(),' ', technician.getLastName()
        ));
    }



    @PostMapping("/profilePhoto/{technicianId}")
    public  ResponseEntity<?> getTechnicianById(@PathVariable Long technicianId,@RequestParam("file") MultipartFile profilePhoto){

        Technician technician=technicianService.loadTechnicianById(technicianId)
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+technicianId));

        try {
            if (profilePhoto == null || profilePhoto.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded.");
            }

            String filename = UUID.randomUUID() + "-" + profilePhoto.getOriginalFilename();
            String projectRoot = System.getProperty("user.dir");
            Path uploadDir = Paths.get(projectRoot, "uploads", "technicians-photos");
            Path filePath = uploadDir.resolve(filename);
            String relativePath = "uploads/technicians-photos/" + filename;

            profilePhoto.transferTo(filePath.toFile());

            technician.setPhotoUrl("http://localhost:9090/"+filePath.toString());
            technicianService.saveTechnician(technician);

            return ResponseEntity.ok("profile de technicien est bien enregistré");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Invalid JSON format: " + e.getMessage());
        }
    }

    @GetMapping("/getProfile/{technicianId}")
    public ResponseEntity<?> getTechnicianProfile(@PathVariable Long technicianId){

        Technician technician=technicianService.loadTechnicianById(technicianId)
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+technicianId));

        UserProfileDTO userProfile= UserProfileDTO.builder()
                .fullName(technician.getFirstName()+" "+technician.getLastName())
                .email(technician.getEmail())
                .phoneNumber(technician.getPhoneNumber())
                .cin(technician.getCin()).build();

        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/editProfile/{technicianId}")
    public ResponseEntity<?> editTechnicianProfile(@PathVariable Long technicianId,@RequestBody UserProfileDTO userProfileUpdates ){

        Technician technician=technicianService.loadTechnicianById(technicianId)
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+technicianId));

        String[] parts = userProfileUpdates.getFullName().trim().split("\\s+");
        String firstName = parts[0];
        String lastName=parts[1];

        technician.setEmail(userProfileUpdates.getEmail());
        technician.setCin(userProfileUpdates.getCin());
        technician.setPhoneNumber(userProfileUpdates.getPhoneNumber());
        technician.setFirstName(firstName);
        technician.setLastName(lastName);
        technician.setCnss(technician.getCnss());
        technician.setId(technician.getId());
        technician.setPhotoUrl(technician.getPhotoUrl());
        technician.setCar(technician.getCar());
        technician.setBonInterventions(technician.getBonInterventions());
        technician.setInterventions(technician.getInterventions());

        technicianService.saveTechnician(technician);

        return ResponseEntity.ok("technician is well updated");
    }
}
