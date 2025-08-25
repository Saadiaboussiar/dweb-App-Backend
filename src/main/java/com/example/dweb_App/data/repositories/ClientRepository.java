package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client,String> {
    Optional<Client> findByFullName(String fullName);
    Optional<Client> findByCin(String cin);
    void deleteByCin(String cin);

}
