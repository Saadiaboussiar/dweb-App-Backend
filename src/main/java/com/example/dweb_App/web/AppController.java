package com.example.dweb_App.web;


import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AppController {
    private AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;

    }


    @PostMapping("/users")
    public ResponseEntity<?> addUsers(@RequestBody AppUser user) {
        log.info("Received user: {}", user);

        if (appService.loadUserBYEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        try {
            appService.addNewUser(user);
            appService.addRoleToUser("USER", user.getEmail());
            return ResponseEntity.ok(Map.of(
                    "roles", appService.getRolesOfUser(user),
                    "email", user.getEmail(),
                    "username", user.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

