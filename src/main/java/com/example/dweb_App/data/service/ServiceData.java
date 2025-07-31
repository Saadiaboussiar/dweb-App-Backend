package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Technician;

import javax.swing.event.CaretListener;

public interface ServiceData {
    public Technician addNewTechnician(Technician technician);
    public Technician loadTechnician(String firstName, String lastName);
    public void addBonToTech(String firstName, String lastName,BonIntervention bon);
    public void addBonToClient(String fullName,BonIntervention bon);
    public Client loadClient(String fullName);
    public BonIntervention assignTechClientToBon(String techFirstName, String techLastName, String ClientFullName, BonIntervention bonIntervention);
    public Technician loadTechnicianByEmail(String email);
    public Technician saveTechnician(Technician technician);
}
