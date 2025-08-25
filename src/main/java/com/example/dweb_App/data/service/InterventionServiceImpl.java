package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.repositories.InterventionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InterventionServiceImpl implements InterventionService {
    private InterventionRepository interventionRepository;

    public InterventionServiceImpl(InterventionRepository interventionRepository) {
        this.interventionRepository = interventionRepository;
    }

    @Override
    public Intervention addNewIntervention(Intervention newIntervention) {
        return interventionRepository.save(newIntervention);
    }

    @Override
    public void deleteIntervention(Long interventionId) {
        interventionRepository.deleteById(interventionId);
    }
}
