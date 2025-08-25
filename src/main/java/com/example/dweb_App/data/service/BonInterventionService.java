package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;

import java.util.List;

public interface BonInterventionService {
    BonIntervention addNewBonIntervention(BonIntervention bonIntervention);
    void deleteBonIntervention(Long bonInterventionId);
    List<BonIntervention> allInterventions();

}
