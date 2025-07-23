package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,String> {
}
