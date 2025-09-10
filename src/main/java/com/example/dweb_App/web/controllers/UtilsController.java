package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.service.TechnicianService;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/test")

public class UtilsController {

    private AppService appService;
    private TechnicianService technicianService;

    public UtilsController(AppService appService, TechnicianService technicianService) {
        this.appService = appService;
        this.technicianService = technicianService;
    }


    @GetMapping("/{email}")
    public ResponseEntity<?> getIdTechnician(@PathVariable String email){

        Technician technician=technicianService.loadTechnicianByEmail(email)
                .orElseThrow(()->new EntityNotFoundException("this technician doesnt exists "+email));

        return ResponseEntity.ok(Map.of("technician Id",technician.getId()));
    }

}
