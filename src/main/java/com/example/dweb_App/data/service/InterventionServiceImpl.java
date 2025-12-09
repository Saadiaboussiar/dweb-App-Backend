package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.*;
import com.example.dweb_App.data.repositories.BonInterventionRepository;
import com.example.dweb_App.data.repositories.InterventionRepository;
import com.example.dweb_App.data.repositories.TechnicianMonthlySummaryRepository;
import com.example.dweb_App.data.repositories.TechnicianRepository;
import com.example.dweb_App.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.example.dweb_App.data.entities.InterventionStatus.REJECTED;
import static com.example.dweb_App.data.entities.InterventionStatus.VALIDATED;

@Service
@Transactional
public class InterventionServiceImpl implements InterventionService {
    private InterventionRepository interventionRepository;
    private BonInterventionRepository bonInterventionRepository;
    private TechnicianMonthlySummaryRepository technicianMonthlySummaryRepository;
    private TechnicianRepository technicianRepository;
    private TechnicianMonthlySummaryService technicianMonthlySummaryService;

    public InterventionServiceImpl(InterventionRepository interventionRepository, BonInterventionRepository bonInterventionRepository, TechnicianMonthlySummaryRepository technicianMonthlySummaryRepository, TechnicianRepository technicianRepository, TechnicianMonthlySummaryService technicianMonthlySummaryService) {
        this.interventionRepository = interventionRepository;
        this.bonInterventionRepository = bonInterventionRepository;
        this.technicianMonthlySummaryRepository = technicianMonthlySummaryRepository;
        this.technicianRepository = technicianRepository;
        this.technicianMonthlySummaryService = technicianMonthlySummaryService;
    }

    @Override
    public Intervention addNewIntervention(Intervention newIntervention) {
        return interventionRepository.save(newIntervention);
    }

    @Override
    public void deleteIntervention(Long interventionId) {
        Intervention intervention=interventionRepository.findById(interventionId)
                .orElseThrow(()->new EntityNotFoundException("intervention not Found "+interventionId));

        if(intervention.getBI()!=null){
            intervention.getBI().setIntervention(null);
            intervention.setBI(null);
            bonInterventionRepository.deleteById(intervention.getBI().getId());

        }
        if(intervention.getTechnician()!=null){
            intervention.getTechnician().getInterventions().remove(intervention);
            intervention.setTechnician(null);
        }
        interventionRepository.deleteById(interventionId);

    }

    @Override
    public List<Intervention> getAllInterventions() {

        return interventionRepository.findAll();
    }

    @Override
    public Optional<Intervention> findInterventionById(Long id) {
        return interventionRepository.findById(id);
    }

    @Override
    public void updateInterventionStatus(Long interId, Boolean isValidate, Long techId) {

        Intervention intervention=interventionRepository.findById(interId)
                .orElseThrow(()->new EntityNotFoundException("Intervention not found "+interId));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
        String frenchDateTime = now.format(formatter);

        intervention.setActionDateTime(frenchDateTime);

        InterventionStatus status;

        if(isValidate==null){
            status= InterventionStatus.PENDING;
        }
        else if(isValidate){

            status= InterventionStatus.VALIDATED;
            updateInterventionPoints( interId, techId);

        }else{
            status= InterventionStatus.REJECTED;
        }
        intervention.setStatus(status);

        interventionRepository.save(intervention);

        System.out.println("Updated intervention " + interId + " to status: " + status);
    }

    @Override
    public void updateInterventionPoints(Long interId, Long techId) {
        // 1. Fetch entities
        Intervention intervention = interventionRepository.findById(interId)
                .orElseThrow(() -> new EntityNotFoundException("Intervention not found " + interId));

        Technician technician = technicianRepository.findById(techId)
                .orElseThrow(() -> new EntityNotFoundException("Technician not found " + techId));

        // Ensure intervention belongs to this technician
        if (!technician.equals(intervention.getTechnician())) {
            throw new IllegalArgumentException(
                    "Intervention " + interId + " does not belong to technician " + techId
            );
        }

        // 2. Determine which date to use
        String dateString = intervention.isUpdated()
                ? intervention.getUpdateDateTime()
                : intervention.getSubmissionDate();

        if (dateString == null) {
            throw new IllegalStateException("No date available for intervention " + interId);
        }

        // 3. Calculate points and bonus
        PointsCategories pointsCategory = PointsCategories.formValue(dateString);
        BigDecimal bonus = pointsCategory.bonusAmount;
        int points = pointsCategory.points;

        // 4. Update intervention
        intervention.setBonusAmount(bonus);
        intervention.setPointsEarned(points);  // ✅ ADDED THIS!

        // 5. Parse date for month/year
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateString, formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateString);
        }

        LocalDate monthStart = dateTime.toLocalDate().withDayOfMonth(1);

        // 6. Find or create monthly summary
        TechnicianMonthlySummary monthlySummary = technicianMonthlySummaryRepository
                .findByTechnicianAndMonthYear(technician, monthStart)
                .orElseGet(() -> technicianMonthlySummaryService.createNewMonthlySummary(technician, monthStart));

        // 7. Update monthly summary
        monthlySummary.setTotalBonus(
                monthlySummary.getTotalBonus().add(bonus)
        );
        monthlySummary.setTotalPoints(
                monthlySummary.getTotalPoints() + points
        );
        monthlySummary.setInterventionsCount(monthlySummary.getInterventionsCount()+1);  // ✅ ADDED

        // 8. Save everything
        technicianMonthlySummaryRepository.save(monthlySummary);
        interventionRepository.save(intervention);

        // Optional: Add to technician's collection for consistency
        if (!technician.getMonthlySummaries().contains(monthlySummary)) {
            technician.getMonthlySummaries().add(monthlySummary);
        }
    }
}
