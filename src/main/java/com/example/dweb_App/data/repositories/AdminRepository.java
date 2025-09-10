package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Admins;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admins,Long> {
    Optional<Admins> findByEmailAndActiveTrue(String email);
}
