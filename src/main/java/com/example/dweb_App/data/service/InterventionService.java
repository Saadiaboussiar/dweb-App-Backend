package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.Intervention;

import java.util.List;

public interface InterventionService {
    Intervention addNewIntervention(Intervention newIntervention);
    void deleteIntervention(Long interventionId);
    List<Intervention> getAllInterventions();
}
