package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.dto.request.TechnicianCreateDTO;

import java.util.List;

public interface TechnicianService {
    Technician addNewTechnician(Technician technician);
    Technician loadTechnician(String firstName, String lastName);
    void addBonToTech(String firstName, String lastName, BonIntervention bon);
    BonIntervention assignTechClientToBon(String techFirstName, String techLastName, String ClientFullName, BonIntervention bonIntervention);
    Technician loadTechnicianByEmail(String email);
    Technician saveTechnician(Technician technician);
    List<Technician> allTechnicians();
    Technician loadTechnicianById(Long id);
    void deleteTechnicianById(Long id);

}
