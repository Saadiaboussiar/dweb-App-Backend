package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.Notifications;
import com.example.dweb_App.data.repositories.NotificationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private NotificationsRepository notificationsRepository;

    public NotificationServiceImpl(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    @Override
    public Notifications saveNotification(Notifications notification) {
        return notificationsRepository.save(notification);
    }

    @Override
    public List<Notifications> getAllNotifications() {
        return notificationsRepository.findAll();
    }

    @Override
    public Optional<Notifications> loadNotificationById(Long id) {
        return notificationsRepository.findById(id);
    }
}
