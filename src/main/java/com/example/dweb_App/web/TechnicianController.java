package com.example.dweb_App.web;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.service.TechnicianService;
import com.example.dweb_App.dto.request.TechnicianCreateDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.exception.UpdateFailedException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return ResponseEntity.ok(addedTechnician.getId());


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


}
