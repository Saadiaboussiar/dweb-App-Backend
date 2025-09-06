package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.repositories.TechnicianRepository;
import com.example.dweb_App.exception.BusinessException;
import com.example.dweb_App.exception.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TechnicianServiceImpl implements TechnicianService {

    private TechnicianRepository technicianRepository;
    private ClientService clientService;
    public TechnicianServiceImpl(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    @Override
    public Technician addNewTechnician(Technician technician) {
        if(technicianRepository.existsByEmail(technician.getEmail())){
            throw new BusinessException(
                    "Email de technicien Exist Déjà",
                    "Un techicien avec cette adresse e-mail exist déjà",
                    "Email de technicien"
            );
        }
        if(technicianRepository.existsByFirstNameAndLastName(technician.getFirstName(),technician.getLastName())){
            throw new BusinessException(
                    "Nom d'utilisateur de technician Exist Déjà",
                    "Un techicien avec ce nom d'utilisateur exist déjà",
                    "Nom d'utilisateur de technicien"
            );
        }
        if(technicianRepository.existsByCin(technician.getCin())){
            throw new BusinessException(
                    "Cin de technicien exist déjà",
                    "un technicien avec ce CIN exit déjà",
                    "CIN de technicien"
            );
        }
        return technicianRepository.save(technician);
    }

    @Override
    public Optional<Technician> loadTechnician(String firstName, String lastName) {
        return technicianRepository.findByFirstNameAndLastName(firstName,lastName);
    }

    @Override
    public void addBonToTech(String firstName, String lastName, BonIntervention bon) {
        Technician tech=loadTechnician(firstName,lastName)
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+firstName+" "+lastName));
        Collection<BonIntervention> bons=tech.getBonInterventions();
        bons.add(bon);
        technicianRepository.save(tech);
    }

    @Override
    public BonIntervention assignTechClientToBon(String techFirstName, String techLastName, String ClientFullName, BonIntervention bonIntervention) {

        Client c=clientService.loadClient(ClientFullName)
                .orElseThrow(()->new EntityNotFoundException("Client not Found "+ClientFullName));
        Technician t=loadTechnician(techFirstName,techLastName)
                .orElseThrow(()->new EntityNotFoundException("Technician not Found "+techFirstName+" "+techLastName));
        bonIntervention.setClient(c);
        bonIntervention.setTechnician(t);

        return bonIntervention;
    }

    @Override
    public Optional<Technician> loadTechnicianByEmail(String email) {
        return technicianRepository.findByEmail(email);

    }

    @Override
    public Technician saveTechnician(Technician technician) {
        return technicianRepository.save(technician);
    }

    @Override
    public List<Technician> allTechnicians() {
        return technicianRepository.findAll();
    }

    @Override
    public Optional<Technician> loadTechnicianById(Long id) {
        return technicianRepository.findById(id);
    }

    @Override
    public void deleteTechnicianById(Long id) {
        technicianRepository.deleteById(id);
    }

    @Override
    public List<BonIntervention> getInterventioonsByTechnician(Long techId) {

        Technician technician=technicianRepository.findById(techId)
                .orElseThrow(()->new EntityNotFoundException("Technician not found"));

        return technician.getBonInterventions().stream().toList();

    }
}
