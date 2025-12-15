package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.entities.TechnicianMonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TechnicianMonthlySummaryRepository extends JpaRepository<TechnicianMonthlySummary,Long> {

    @Query("SELECT tms FROM TechnicianMonthlySummary tms " +
            "WHERE tms.technician.id = :technicianId " +
            "AND tms.monthYear = :currentMonth")
    Optional<TechnicianMonthlySummary> findByTechnicianIdAndMonthYear(
            @Param("technicianId") Long technicianId,
            @Param("currentMonth") LocalDate currentMonth);
}
