package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.Admins;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.repositories.AdminRepository;
import com.example.dweb_App.data.repositories.TechnicianRepository;
import com.example.dweb_App.dto.response.UserProfileDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import com.example.dweb_App.utils.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/user")
public class AppUserController {
    private AppService appService;
    private ProfileService profileService;
    private AdminRepository adminRepository;
    private TechnicianRepository technicianRepository;

    public AppUserController(AppService appService, ProfileService profileService, AdminRepository adminRepository, TechnicianRepository technicianRepository) {
        this.appService = appService;
        this.profileService = profileService;
        this.adminRepository = adminRepository;
        this.technicianRepository = technicianRepository;
    }

    @DeleteMapping("/{userEmail}")
    public ResponseEntity<?> deleteUser(@PathVariable String userEmail){

        AppUser user=appService.loadUserBYEmail(userEmail)
                .orElseThrow(()->new EntityNotFoundException("User Not Found: "+userEmail));

        appService.deleteUserById(user.getId());

        return ResponseEntity.ok(Map.of("Technicain deleted: ",userEmail));

    }

    @PostMapping(path="/profilePhoto/{userEmail}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUserProfile(
            @PathVariable String userEmail,
            @RequestParam("profilePhoto") MultipartFile profilePhoto) {

        try {
            if (profilePhoto == null || profilePhoto.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded.");
            }

            // 1. First try to find as Admin
            Optional<Admins> adminOpt = adminRepository.findByEmailAndActiveTrue(userEmail);
            if (adminOpt.isPresent()) {
                Admins admin = adminOpt.get();
                String filename = profileService.saveProfilePhoto(profilePhoto, "admins-profiles");
                admin.setProfileUrl("http://localhost:9090/uploads/admins-profiles/" + filename);
                adminRepository.save(admin);
                return ResponseEntity.ok("Admin profile photo updated successfully");
            }

            // 2. Then try to find as Technician
            Optional<Technician> technicianOpt = technicianRepository.findByEmail(userEmail);
            if (technicianOpt.isPresent()) {
                Technician technician = technicianOpt.get();
                String filename = profileService.saveProfilePhoto(profilePhoto, "technicians-profiles");
                technician.setPhotoUrl("http://localhost:9090/uploads/technicians-profiles/" + filename);
                technicianRepository.save(technician);
                return ResponseEntity.ok("Technician profile photo updated successfully");
            }

            // 3. If neither found, return error
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with email: " + userEmail);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }


    @GetMapping("/getProfile/{userEmail}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userEmail) {

        try {
            UserProfileDTO userProfile=profileService.getProfileData(userEmail);

            return ResponseEntity.ok(userProfile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving profile: " + e.getMessage());
        }
    }

    @PutMapping("/editProfile/{userEmail}")
    public ResponseEntity<?> editUserProfile(
            @PathVariable String userEmail,
            @RequestBody UserProfileDTO userProfileUpdates) {

        try {
            // 1. Try to find as Admin first
            Optional<Admins> adminOpt = adminRepository.findByEmailAndActiveTrue(userEmail);
            if (adminOpt.isPresent()) {
                Admins admin = adminOpt.get();

                // Update admin fields (add these fields to your Admin entity if needed)
                admin.setEmail(userProfileUpdates.getEmail());
                admin.setUsername(userProfileUpdates.getFullName()); // or split if you have first/last
                admin.setProfileUrl(userProfileUpdates.getProfileUrl());

                adminRepository.save(admin);
                return ResponseEntity.ok("Admin profile updated successfully");
            }

            // 2. Try to find as Technician
            Optional<Technician> technicianOpt = technicianRepository.findByEmail(userEmail);
            if (technicianOpt.isPresent()) {
                Technician technician = technicianOpt.get();

                // Split full name into first and last name
                String[] parts = userProfileUpdates.getFullName().trim().split("\\s+", 2);
                String firstName = parts[0];
                String lastName = parts.length > 1 ? parts[1] : "";

                technician.setEmail(userProfileUpdates.getEmail());
                technician.setPhoneNumber(userProfileUpdates.getPhoneNumber());
                technician.setFirstName(firstName);
                technician.setLastName(lastName);
                technician.setPhotoUrl(userProfileUpdates.getProfileUrl());

                technicianRepository.save(technician);
                return ResponseEntity.ok("Technician profile updated successfully");
            }

            // 3. User not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with email: " + userEmail);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating profile: " + e.getMessage());
        }
    }

    @GetMapping("/userId/{userEmail}")
    public ResponseEntity<Long> getUserId(@PathVariable String userEmail){

        Optional<Admins> admin = adminRepository.findByEmailAndActiveTrue(userEmail);

        if (admin.isPresent()) {
            return ResponseEntity.ok(admin.get().getId());
        }

        // Check if the email belongs to a technician
        Optional<Technician> technician = technicianRepository.findByEmail(userEmail);

        if (technician.isPresent()) {
            return ResponseEntity.ok(technician.get().getId());
        }
        return null;
    }
}
