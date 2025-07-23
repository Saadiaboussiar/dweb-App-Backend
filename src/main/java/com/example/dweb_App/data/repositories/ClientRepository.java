package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,String> {
}
