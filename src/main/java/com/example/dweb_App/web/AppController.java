package com.example.dweb_App.web;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.repositories.BonInterventionRepository;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AppController {
    private AppService appService;
    private BonInterventionRepository bonInterventionRepository;
    public AppController(AppService appService, BonInterventionRepository bonInterventionRepository) {
        this.appService = appService;
        this.bonInterventionRepository = bonInterventionRepository;
    }


    @PostMapping(path = "/users")
    public ResponseEntity<?> addUsers(@RequestBody AppUser user){
        try {
            appService.addNewUser(user);
            appService.addRoleToUser("USER",user.getUsername());
            return ResponseEntity.ok("User saved.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }

    }

    @PostMapping(path = "/bonIntervention")
    public ResponseEntity<?> addBon(@RequestBody BonIntervention bonIntervention){
       bonInterventionRepository.save(bonIntervention);
       return ResponseEntity.ok("Intervention infos saved.");

    }



}
