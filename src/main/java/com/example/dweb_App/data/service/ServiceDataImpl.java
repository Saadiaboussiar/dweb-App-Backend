package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.repositories.ClientRepository;
import com.example.dweb_App.data.repositories.TechnicianRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class ServiceDataImpl implements ServiceData {
    private TechnicianRepository technicianRepository;
    private ClientRepository clientRepository;

    public ServiceDataImpl(TechnicianRepository technicianRepository, ClientRepository clientRepository) {
        this.technicianRepository = technicianRepository;
        this.clientRepository = clientRepository;
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
    public void addBonToClient(String fullName, BonIntervention bon) {
        Client client=loadClient(fullName);
        Collection<BonIntervention> bons=client.getBonInterventions();
        bons.add(bon);
        clientRepository.save(client);
    }

    @Override

    public Client loadClient(String fullName) {
        return clientRepository.findByFullName(fullName);
    }

    @Override
    public BonIntervention assignTechClientToBon(String techFirstName, String techLastName, String clientFullName, BonIntervention bonIntervention) {
        Client c=loadClient(clientFullName);
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
    public Client addNewClient(Client client) {
        return clientRepository.save(client);
    }
}
