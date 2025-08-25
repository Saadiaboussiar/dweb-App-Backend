package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechnicianRepository extends JpaRepository<Technician,Long> {
    Optional<Technician> findByFirstNameAndLastName(String firstName, String lastName);
    Optional<Technician> findByEmail(String email);

}
