package com.example.dweb_App.web;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.repositories.BonInterventionRepository;
import com.example.dweb_App.data.service.ServiceData;
import com.example.dweb_App.dto.request.TechnicianCreateDTO;
import com.example.dweb_App.dto.response.TechnicianResponseDTO;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.UUID;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AppController {
    private AppService appService;
    private BonInterventionRepository bonInterventionRepository;
    private ServiceData serviceData;
    public AppController(AppService appService, BonInterventionRepository bonInterventionRepository, ServiceData serviceData) {
        this.appService = appService;
        this.bonInterventionRepository = bonInterventionRepository;
        this.serviceData = serviceData;

    }


    @PostMapping(path = "/users")
    public ResponseEntity<?> addUsers(@RequestBody AppUser user){
        try {
            appService.addNewUser(user);
            appService.addRoleToUser("USER",user.getEmail());
            return ResponseEntity.ok("User saved.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }

    }

    /*
    @PostMapping(path = "/bonIntervention")
    public ResponseEntity<?> addBon(@ModelAttribute BonForm bonForm, @RequestParam("bonImage") MultipartFile bonImage){
        try{
            bonImage.transferTo(new File("uploads/" + bonImage.getOriginalFilename()));
        }catch(IOException e){
            e.printStackTrace();
        }

        Technician tech=serviceData.loadTechnician(bonForm.getTechnicianFN(),bonForm.getTechnicianLN());
        Client client=serviceData.loadClient(bonForm.getClient());
        LocalDate date = LocalDate.parse(bonForm.getDate());
        Date utilDate=Date.from((date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        BonIntervention bonIntervention= BonIntervention.builder()
                .client(client)
                .technician(tech)
                .duration(bonForm.getDuration())
                .km(bonForm.getKm())
                .date(utilDate);
                .finishTime(bonForm.getFinishTime())
                .startTime(bonForm.getStartTme())
                .ville(bonForm.getVille())
                .numberIntervenant(bonForm.getNbreIntervenant())
                .bonImageUrl("uploads/" + bonImage.getOriginalFilename()).build();
       BonIntervention bonInter=bonInterventionRepository.save(bonIntervention);

       return ResponseEntity.ok("Intervention infos saved.");

    }
     */

    @PostMapping(path = "/technicianProfile")
    public ResponseEntity<?> saveTechnicianInfos(
            @ModelAttribute TechnicianCreateDTO technicianInfos,
            @RequestParam("profileImage") MultipartFile profileImage) {

        if (profileImage.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded.");
        }

        String filename = UUID.randomUUID() + "-" + profileImage.getOriginalFilename();
        String projectRoot = System.getProperty("user.dir");
        Path uploadDir = Paths.get(projectRoot, "uploads", "technicians-profiles");
        Path filePath = uploadDir.resolve(filename);
        File file=new File(uploadDir+filename);

        String relativePath = "uploads/technicians-profiles/" + filename;

        // Step 1: Save image
        try {

            Files.createDirectories(uploadDir); // creates folder only if missing

            if(file.exists()){
                System.out.println("File already exists: " + file.getAbsolutePath());
            }else {
                profileImage.transferTo(filePath.toFile());
                System.out.println("Image saved to: " + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving image: " + e.getMessage());
        }

        // Step 2: Save technician
        try {
            Technician tech = Technician.builder()
                    .lastName(technicianInfos.getLastName())
                    .firstName(technicianInfos.getFirstName())
                    .email(technicianInfos.getEmail())
                    .cin(technicianInfos.getCin())
                    .cnss(technicianInfos.getCnss())
                    .phoneNumber(technicianInfos.getPhoneNumber())
                    .bonInterventions(new ArrayList<>())
                    .interventions(new ArrayList<>())
                    .car(null)
                    .photoUrl(relativePath)
                    .build();

            serviceData.addNewTechnician(tech);
            System.out.println("Technician saved: " + tech.getFirstName() + " " + tech.getLastName());

            return ResponseEntity.ok("Technician profile and info saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving technician info: " + e.getMessage());
        }
    }

    @GetMapping(path = "/technicianProfile")
    public ResponseEntity<TechnicianResponseDTO> displayTechnicianInfos(Principal principal) {

            String email = principal.getName();
            Technician technician = serviceData.loadTechnicianByEmail(email);
            if(technician == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            TechnicianResponseDTO responseDTO = TechnicianResponseDTO.builder()
                    .firstName(technician.getFirstName())
                    .lastName(technician.getLastName())
                    .email(technician.getEmail())
                    .phoneNumber(technician.getPhoneNumber())
                    .cin(technician.getCin())
                    .cnss(technician.getCnss())
                    .profileUrl("http://localhost:9090/" + technician.getPhotoUrl())
                    .build();
            return ResponseEntity.ok(responseDTO);

    }

    @PutMapping(path="/technicianProfile")
    public ResponseEntity<?> updateTechProfile(@ModelAttribute TechnicianCreateDTO technicianInfos,
                                               @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                               Principal principal){
        String email=principal.getName();
        Technician technician=serviceData.loadTechnicianByEmail(email);
        try {
            technician.setFirstName(technicianInfos.getFirstName());
            technician.setLastName(technicianInfos.getLastName());
            technician.setEmail(technicianInfos.getEmail());
            technician.setPhoneNumber(technicianInfos.getPhoneNumber());
            technician.setCin(technicianInfos.getCin());
            technician.setCnss(technicianInfos.getCnss());


            if (profileImage!=null && !profileImage.isEmpty()) {
                String projectRoot = System.getProperty("user.dir");

                Path oldImagePath = Paths.get(projectRoot).resolve(technician.getPhotoUrl());
                    File oldImage = oldImagePath.toFile();
                    if (oldImage.exists()) {
                        oldImage.delete();
                    }
                    Path uploadDir = Paths.get(projectRoot, "uploads", "technicians-profiles");
                    String filename = UUID.randomUUID() + "-" + profileImage.getOriginalFilename();
                    File file = uploadDir.resolve(filename).toFile();

                    profileImage.transferTo(file);

                    technician.setPhotoUrl("uploads/technicians-profiles/" + filename);


            }else{
                technician.setPhotoUrl(technician.getPhotoUrl());
            }

            serviceData.saveTechnician(technician);

            return ResponseEntity.ok("Profile updated successfully");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while updating technician infos: " + e.getMessage());

        }


    }


}
