package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;

import java.awt.font.OpenType;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    void addBonToClient(String fullName, BonIntervention bon);
    Optional<Client> loadClient(String fullName);
    Client saveClient(Client client);
    List<Client> allClients();
    Optional<Client> loadClientByCin(String cin);
    void deleteClientByCin(String cin);

}
