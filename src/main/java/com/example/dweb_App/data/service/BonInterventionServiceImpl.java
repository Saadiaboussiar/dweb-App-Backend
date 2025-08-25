package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.repositories.BonInterventionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BonInterventionServiceImpl implements BonInterventionService {

    private BonInterventionRepository bonInterventionRepository;

    public BonInterventionServiceImpl(BonInterventionRepository bonInterventionRepository) {
        this.bonInterventionRepository = bonInterventionRepository;
    }

    @Override
    public BonIntervention addNewBonIntervention(BonIntervention bonIntervention) {
        return bonInterventionRepository.save(bonIntervention);
    }

    @Override
    public void deleteBonIntervention(Long bonInterventionId) {

        bonInterventionRepository.deleteById(bonInterventionId);
    }

    @Override
    public List<BonIntervention> allInterventions() {
        return bonInterventionRepository.findAll();
    }
}
