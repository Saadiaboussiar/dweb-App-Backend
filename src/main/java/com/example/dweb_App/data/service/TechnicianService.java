package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.dto.request.TechnicianCreateDTO;

import java.util.List;
import java.util.Optional;

public interface TechnicianService {
    Technician addNewTechnician(Technician technician);
    Optional<Technician> loadTechnician(String firstName, String lastName);
    void addBonToTech(String firstName, String lastName, BonIntervention bon);
    BonIntervention assignTechClientToBon(String techFirstName, String techLastName, String ClientFullName, BonIntervention bonIntervention);
    Optional<Technician> loadTechnicianByEmail(String email);
    Technician saveTechnician(Technician technician);
    List<Technician> allTechnicians();
    Optional<Technician> loadTechnicianById(Long id);
    void deleteTechnicianById(Long id);
    List<BonIntervention> getInterventioonsByTechnician(Long techId);
}
