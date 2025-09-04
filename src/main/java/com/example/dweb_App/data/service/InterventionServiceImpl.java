package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.entities.InterventionStatus;
import com.example.dweb_App.data.entities.PointsCategories;
import com.example.dweb_App.data.repositories.BonInterventionRepository;
import com.example.dweb_App.data.repositories.InterventionRepository;
import com.example.dweb_App.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public InterventionServiceImpl(InterventionRepository interventionRepository, BonInterventionRepository bonInterventionRepository) {
        this.interventionRepository = interventionRepository;
        this.bonInterventionRepository = bonInterventionRepository;
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
    public void updateInterventionStatus(Long interId, Boolean isValidate) {

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
            updateInterventionPoints( interId);

        }else{
            status= InterventionStatus.REJECTED;
        }
        intervention.setStatus(status);
        intervention.setActionDateTime(frenchDateTime);

        interventionRepository.save(intervention);

        System.out.println("Updated intervention " + interId + " to status: " + status);
    }

    @Override
    public void updateInterventionPoints(Long interId) {
        Intervention intervention=interventionRepository.findById(interId)
                .orElseThrow(()->new EntityNotFoundException("Intervetion not found "+interId));

        String submissionDate=intervention.getSubmissionDate();
        PointsCategories pointsCategory=PointsCategories.formValue(submissionDate);

        int points=pointsCategory.bonusAmount;

        intervention.setPoints(points);

        interventionRepository.save(intervention);


    }
}
