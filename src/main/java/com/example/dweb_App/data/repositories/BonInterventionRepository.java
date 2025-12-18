package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.BonIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BonInterventionRepository extends JpaRepository<BonIntervention,Long> {

    @Query("SELECT e FROM BonIntervention e WHERE e.date LIKE CONCAT(:year, '-', :month, '-%')")
    List<BonIntervention> findByYearAndMonth(@Param("year") String year,
                                        @Param("month") String month);
}
