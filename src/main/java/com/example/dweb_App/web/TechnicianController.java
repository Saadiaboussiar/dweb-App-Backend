package com.example.dweb_App.web;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.service.TechnicianService;
import com.example.dweb_App.dto.request.TechnicianCreateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/technicianInfos")

public class TechnicianController {
    private TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }


    @GetMapping
    public ResponseEntity<List<Technician>> getAllTechnicians(){
       List<Technician> technicians=technicianService.allTechnicians();
       if(technicians!=null){
            return ResponseEntity.ok(technicians);
        }
       else{
           return ResponseEntity.noContent().build();
       }
    }
    @PostMapping
    public ResponseEntity<?> addNewTechnician(@RequestBody TechnicianCreateDTO technician){

        if(technicianService.loadTechnician(technician.getFirstName(), technician.getLastName())!=null){
            return ResponseEntity.ok("Technician already exists");
        }
        Technician newTechnician= Technician.builder().cin(technician.getCin()).email(technician.getEmail()).cnss(technician.getCnss()).firstName(technician.getFirstName()).lastName(technician.getLastName()).phoneNumber(technician.getPhoneNumber()).build();
        Technician addedTechnician=technicianService.addNewTechnician(newTechnician);
        return ResponseEntity.ok(addedTechnician.getId());

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTechnician(@PathVariable Long id, @RequestBody TechnicianCreateDTO technician){
        Technician existingTechnician=technicianService.loadTechnicianById(id);
        if(existingTechnician==null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Technician not found"));

        }
        try{
            existingTechnician.setCnss(technician.getCnss());
            existingTechnician.setCin(technician.getCin());
            existingTechnician.setEmail(technician.getEmail());
            existingTechnician.setLastName(technician.getLastName());
            existingTechnician.setFirstName(technician.getFirstName());
            existingTechnician.setPhoneNumber(technician.getPhoneNumber());

            Technician updatedTechnician=technicianService.saveTechnician(existingTechnician);

            return ResponseEntity.ok(updatedTechnician);
        }catch(Exception e){
            log.error("Failed to update client CIN: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while editing client infos: " + e.getMessage());

        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTechnician(@PathVariable Long id){
        Technician technician=technicianService.loadTechnicianById(id);
        if(technician==null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Technician not found"));

        }
        technicianService.deleteTechnicianById(id);
        return ResponseEntity.ok(Map.of(
                "message", "Client deleted successfully",
                "clientName", technician.getFirstName(),' ', technician.getLastName()
        ));
    }


}
