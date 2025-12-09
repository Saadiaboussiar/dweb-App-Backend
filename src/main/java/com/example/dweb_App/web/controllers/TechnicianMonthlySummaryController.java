package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.entities.TechnicianMonthlySummary;
import com.example.dweb_App.data.repositories.TechnicianMonthlySummaryRepository;
import com.example.dweb_App.data.repositories.TechnicianRepository;
import com.example.dweb_App.data.service.TechnicianMonthlySummaryService;
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

    @GetMapping("/{technicianId}")
    public ResponseEntity<TechnicianMonthlySummary> getTechnicianMonthlySummary(@PathVariable Long technicianId){

        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);


        TechnicianMonthlySummary monthlySummary = technicianMonthlySummaryRepository
                .findByTechnicianIdAndMonthYear(technicianId, currentMonth)
                .orElseThrow(()->new EntityNotFoundException("Summary Not Found "));

        return ResponseEntity.ok(monthlySummary);
    }
}
