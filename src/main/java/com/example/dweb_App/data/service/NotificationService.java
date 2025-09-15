package com.example.dweb_App.data.service;

import com.example.dweb_App.data.entities.Notifications;

import javax.management.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationService {

    Notifications saveNotification(Notifications notification);
    List<Notifications> getAllNotifications();
    Optional<Notifications> loadNotificationById(Long id);
    void deleteNotification(Long notificationId);
    void deleteAllNotifications();
}
