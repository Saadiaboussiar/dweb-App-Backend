package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterventionRepository extends JpaRepository<Intervention,Long> {
}
