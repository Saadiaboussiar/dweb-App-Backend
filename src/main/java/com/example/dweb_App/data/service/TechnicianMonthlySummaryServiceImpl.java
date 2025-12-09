package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.entities.TechnicianMonthlySummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
@Service
@Transactional
public class TechnicianMonthlySummaryServiceImpl implements TechnicianMonthlySummaryService {
    @Override
    public TechnicianMonthlySummary createNewMonthlySummary(Technician technician, LocalDate monthStart) {
        TechnicianMonthlySummary summary = new TechnicianMonthlySummary();
        summary.setTechnician(technician);
        summary.setMonthYear(monthStart);
        summary.setTotalPoints(0);
        summary.setTotalBonus(BigDecimal.ZERO);
        summary.setInterventionsCount(0);
        return summary;

    }
}
