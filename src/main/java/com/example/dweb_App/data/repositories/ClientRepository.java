package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,String> {
    Client findByFullName(String fullName);
    Client findByCin(String cin);
    void deleteByCin(String cin);

}
