package com.example.dweb_App.data.repositories;

import com.example.dweb_App.data.entities.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notifications,Long> {

}
