package com.example.dweb_App.web;

import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AppController {
    private AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
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


}
