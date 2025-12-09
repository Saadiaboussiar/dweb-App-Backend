package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.entities.TechnicianMonthlySummary;

import java.time.LocalDate;

public interface TechnicianMonthlySummaryService {
    TechnicianMonthlySummary createNewMonthlySummary(Technician technician, LocalDate monthStart);
}
