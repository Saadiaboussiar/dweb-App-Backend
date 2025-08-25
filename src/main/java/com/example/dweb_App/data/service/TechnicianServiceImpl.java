package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.repositories.TechnicianRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

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
        return technicianRepository.save(technician);
    }

    @Override
    public Technician loadTechnician(String firstName, String lastName) {
        return technicianRepository.findByFirstNameAndLastName(firstName,lastName);
    }

    @Override
    public void addBonToTech(String firstName, String lastName, BonIntervention bon) {
        Technician tech=loadTechnician(firstName,lastName);
        Collection<BonIntervention> bons=tech.getBonInterventions();
        bons.add(bon);
        technicianRepository.save(tech);
    }

    @Override
    public BonIntervention assignTechClientToBon(String techFirstName, String techLastName, String ClientFullName, BonIntervention bonIntervention) {

        Client c=clientService.loadClient(ClientFullName);
        Technician t=loadTechnician(techFirstName,techLastName);
        bonIntervention.setClient(c);
        bonIntervention.setTechnician(t);

        return bonIntervention;
    }

    @Override
    public Technician loadTechnicianByEmail(String email) {
        Technician technician=technicianRepository.findByEmail(email);
        if(technician==null){
            throw new UsernameNotFoundException("Technician not found with email: " + email);
        }
        return technician;
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
    public Technician loadTechnicianById(Long id) {
        return technicianRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteTechnicianById(Long id) {
        technicianRepository.deleteById(id);
    }
}
