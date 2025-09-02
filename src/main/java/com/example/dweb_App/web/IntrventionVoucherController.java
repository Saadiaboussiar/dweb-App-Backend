package com.example.dweb_App.web;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.service.BonInterventionService;
import com.example.dweb_App.data.service.ClientService;
import com.example.dweb_App.data.service.InterventionService;
import com.example.dweb_App.data.service.TechnicianService;
import com.example.dweb_App.dto.request.BonInterventionCreateDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/bonIntervention")

public class IntrventionVoucherController {

    private BonInterventionService bonInterventionService;
    private ClientService clientService;
    private TechnicianService technicianService;
    private InterventionService interventionService;

    public IntrventionVoucherController(BonInterventionService bonInterventionService, ClientService clientService, TechnicianService technicianService, InterventionService interventionService) {
        this.bonInterventionService = bonInterventionService;
        this.clientService = clientService;
        this.technicianService = technicianService;
        this.interventionService = interventionService;
    }

    @PreAuthorize("hasAuthority('USER')")

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addNewBonIntervention(@RequestPart("bonIntervention") String bonInterventionJson, @RequestParam(value = "bonImage", required = false) MultipartFile bonImage,  HttpServletRequest request){

        try {
        ObjectMapper objectMapper = new ObjectMapper();
        BonInterventionCreateDTO bonIntervention = objectMapper.readValue(bonInterventionJson, BonInterventionCreateDTO.class);

        log.info("Content-Type: {}", request.getContentType());
        log.info("bonIntervention received: {}", bonIntervention);
        log.info("bonImage received: {}", bonImage != null ? bonImage.getOriginalFilename() : "null");

        if (bonImage == null || bonImage.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded.");
        }

        Client client=clientService.loadClient(bonIntervention.getClient())
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+bonIntervention.getClient()));

        Technician technician=technicianService.loadTechnician(bonIntervention.getTechnicianFN(),bonIntervention.getTechnicianLN())
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+bonIntervention.getTechnicianFN()+" "+bonIntervention.getTechnicianLN()));


        String filename = UUID.randomUUID() + "-" + bonImage.getOriginalFilename();
        String projectRoot = System.getProperty("user.dir");
        Path uploadDir = Paths.get(projectRoot, "uploads", "bon-photos");
        Path filePath = uploadDir.resolve(filename);
        String relativePath = "uploads/bon-photos/" + filename;



            bonImage.transferTo(filePath.toFile());

            BonIntervention newBonIntervention = BonIntervention.builder()
                    .client(client)
                    .technician(technician)
                    .ville(bonIntervention.getVille())
                    .km(bonIntervention.getKm())
                    .date(bonIntervention.getDate())
                    .startTime(bonIntervention.getStartTime())
                    .finishTime(bonIntervention.getFinishTime())
                    .duration(bonIntervention.getDuration())
                    .numberIntervenant(bonIntervention.getNbreIntervenant())
                    .bonImageUrl("http://localhost:9090/"+relativePath)
                    .build();

            BonIntervention savedBonIntervention=bonInterventionService.addNewBonIntervention(newBonIntervention);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMMM/yyyy HH'h'mm", Locale.FRENCH);
            String frenchDateTime = now.format(formatter);

            Intervention intervention= Intervention.builder()
                    .BI(savedBonIntervention)
                    .technician(technician)
                    .submissionDate(frenchDateTime)
                    .build();

            Intervention savedIntervention=interventionService.addNewIntervention(intervention);

            return ResponseEntity.ok(savedBonIntervention.getId());

        }catch (Exception e){

            return ResponseEntity.badRequest().body("Invalid JSON format: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BonIntervention>> getBonIntervention(){
        List<BonIntervention> bonInterventions=bonInterventionService.allInterventions();
        if(!bonInterventions.isEmpty()){
            return ResponseEntity.ok(bonInterventions);
        }
        else return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("Technician/{technicianId}")
    public  ResponseEntity<?> getBonInterventionForTechnician(@PathVariable Long technicianId){
        Technician technician=technicianService.loadTechnicianById(technicianId)
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+technicianId));

        List<BonIntervention> bonInterventionList=technician.getBonInterventions().stream().toList();
        if(!bonInterventionList.isEmpty()){
            return ResponseEntity.ok(bonInterventionList);
        }else return ResponseEntity.noContent().build();

    }




}
