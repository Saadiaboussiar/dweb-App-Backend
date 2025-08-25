package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician,Long> {
    Technician findByFirstNameAndLastName(String firstName, String lastName);
    Technician findByEmail(String email);

}
