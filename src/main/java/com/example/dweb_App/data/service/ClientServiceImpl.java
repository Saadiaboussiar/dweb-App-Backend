package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.repositories.ClientRepository;
import com.example.dweb_App.exception.BusinessException;
import com.example.dweb_App.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    private ClientRepository clientRepository;
    private TechnicianService technicianService;
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void addBonToClient(String fullName, BonIntervention bon) {
        Client client=loadClient(fullName)
                .orElseThrow(()->new EntityNotFoundException("Client not Found "+fullName));
        Collection<BonIntervention> bons=client.getBonInterventions();
        bons.add(bon);
        clientRepository.save(client);
    }

    @Override
    public Optional<Client> loadClient(String fullName) {
        return clientRepository.findByFullName(fullName);
    }

    @Override
    public Client saveClient(Client client) {
        if(clientRepository.existsByEmail(client.getEmail())){
            throw new BusinessException(
                    "Email de client Exist Déjà",
                    "Un client avec cette adresse e-mail exist déjà",
                    "Email de client"
            );
        }
        if(clientRepository.existsByFullName(client.getFullName())){
            throw new BusinessException(
                    "Nom d'utilisateur Exist Déjà",
                    "Un client avec ce nom d'utilisateur exist déjà",
                    "Nom d'utilisateur de client"
            );
        }
        if(clientRepository.existsById(client.getCin())){
            throw new BusinessException(
                    "CIN Exist Déjà",
                    "Un client avec ce CIN exist déjà",
                    "CIN de client"
            );
        }
        return clientRepository.save(client);
    }

    @Override
    public List<Client> allClients() {
        return clientRepository.findAll();
    }

    @Override
    public Optional<Client> loadClientByCin(String cin) {
        return clientRepository.findByCin(cin);
    }

    @Override
    public void deleteClientByCin(String cin) {
        clientRepository.deleteById(cin);
    }

}
