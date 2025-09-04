package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.Intervention;

import java.util.List;
import java.util.Optional;

public interface InterventionService {
    Intervention addNewIntervention(Intervention newIntervention);
    void deleteIntervention(Long interventionId);
    List<Intervention> getAllInterventions();
    Optional<Intervention> findInterventionById(Long id);
    void updateInterventionStatus(Long interId, Boolean isValidate);
    void updateInterventionPoints(Long interId);

}
