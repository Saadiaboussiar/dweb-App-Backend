package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.entities.TechnicianMonthlySummary;
import com.example.dweb_App.data.repositories.TechnicianMonthlySummaryRepository;
import com.example.dweb_App.data.repositories.TechnicianRepository;
import com.example.dweb_App.data.service.TechnicianMonthlySummaryService;
import com.example.dweb_App.dto.response.MonthlySummaryResponseDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/technicianMonthlySummary")
public class TechnicianMonthlySummaryController {

    private TechnicianMonthlySummaryService technicianMonthlySummaryService;
    private TechnicianMonthlySummaryRepository technicianMonthlySummaryRepository;
    private TechnicianRepository technicianRepository;

    public TechnicianMonthlySummaryController(TechnicianMonthlySummaryService technicianMonthlySummaryService, TechnicianMonthlySummaryRepository technicianMonthlySummaryRepository, TechnicianRepository technicianRepository) {
        this.technicianMonthlySummaryService = technicianMonthlySummaryService;
        this.technicianMonthlySummaryRepository = technicianMonthlySummaryRepository;
        this.technicianRepository = technicianRepository;
    }

    @GetMapping("/current/{technicianId}")
    public ResponseEntity<MonthlySummaryResponseDTO> getCurrentMonthSummary(@PathVariable Long technicianId){

        if (!technicianRepository.existsById(technicianId)) {
            throw new EntityNotFoundException("Technician not found with id: " + technicianId);
        }
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        TechnicianMonthlySummary monthlySummary = technicianMonthlySummaryRepository
                .findByTechnicianIdAndMonthYear(technicianId, currentMonth)
                .orElseThrow(()->new EntityNotFoundException("Summary Not Found "));

        MonthlySummaryResponseDTO responseDTO=MonthlySummaryResponseDTO.builder()
                .id(monthlySummary.getId())
                .technicianId(monthlySummary.getTechnician().getId())  // Get ID from technician
                .monthYear(monthlySummary.getMonthYear())
                .totalPoints(monthlySummary.getTotalPoints())
                .totalBonus(monthlySummary.getTotalBonus())
                .interventionsCount(monthlySummary.getInterventionsCount())
                .lastUpdated(monthlySummary.getLastUpdated())
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{month}/{year}/{technicianId}")
    public ResponseEntity<MonthlySummaryResponseDTO> getMonthlySummary(@PathVariable int month, @PathVariable int year, @PathVariable Long technicianId){

        if (!technicianRepository.existsById(technicianId)) {
            throw new EntityNotFoundException("Technician not found with id: " + technicianId);
        }
        LocalDate monthYear = LocalDate.of(year, month, 1);

        TechnicianMonthlySummary monthlySummary = technicianMonthlySummaryRepository
                .findByTechnicianIdAndMonthYear(technicianId, monthYear)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("No summary found for technician %d in %d-%02d",
                                technicianId, year, month)));

        MonthlySummaryResponseDTO responseDTO=MonthlySummaryResponseDTO.builder()
                .id(monthlySummary.getId())
                .technicianId(monthlySummary.getTechnician().getId())  // Get ID from technician
                .monthYear(monthlySummary.getMonthYear())
                .totalPoints(monthlySummary.getTotalPoints())
                .totalBonus(monthlySummary.getTotalBonus())
                .interventionsCount(monthlySummary.getInterventionsCount())
                .lastUpdated(monthlySummary.getLastUpdated())
                .build();

        return ResponseEntity.ok(responseDTO);

    }
}
