package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.repositories.BonInterventionRepository;
import com.example.dweb_App.data.repositories.InterventionRepository;
import com.example.dweb_App.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BonInterventionServiceImpl implements BonInterventionService {

    private BonInterventionRepository bonInterventionRepository;
    private InterventionRepository interventionRepository;
    public BonInterventionServiceImpl(BonInterventionRepository bonInterventionRepository, InterventionRepository interventionRepository) {
        this.bonInterventionRepository = bonInterventionRepository;
        this.interventionRepository = interventionRepository;
    }

    @Override
    public BonIntervention addNewBonIntervention(BonIntervention bonIntervention) {
        return bonInterventionRepository.save(bonIntervention);
    }

    @Override
    public void deleteBonIntervention(Long bonInterventionId) {

        BonIntervention bonIntervention=bonInterventionRepository.findById(bonInterventionId)
                .orElseThrow(()->new EntityNotFoundException("intervention not Found "+bonInterventionId));

        if(bonIntervention.getIntervention()!=null){
            bonIntervention.getIntervention().setBI(null);
            bonIntervention.setIntervention(null);
            interventionRepository.deleteById(bonIntervention.getIntervention().getId());
        }
        if(bonIntervention.getTechnician()!=null){
            bonIntervention.getTechnician().getBonInterventions().remove(bonIntervention);
            bonIntervention.setTechnician(null);
        }
        if(bonIntervention.getClient()!=null){
            bonIntervention.getClient().getBonInterventions().remove(bonIntervention);
            bonIntervention.setClient(null);
        }

        bonInterventionRepository.deleteById(bonInterventionId);
    }

    @Override
    public List<BonIntervention> allInterventions() {
        return bonInterventionRepository.findAll();
    }
}
