package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;

import java.util.List;

public interface ClientService {
    void addBonToClient(String fullName, BonIntervention bon);
    Client loadClient(String fullName);
    Client addNewClient(Client client);
    List<Client> allClients();
    Client loadClientByCin(String cin);
    void deleteClientByCin(String cin);
    Client saveClient(Client client);

}
