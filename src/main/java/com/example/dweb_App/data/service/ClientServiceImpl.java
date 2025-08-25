package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.repositories.ClientRepository;
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
        clientRepository.deleteByCin(cin);
    }

}
