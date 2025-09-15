package com.example.dweb_App.web.controllers;


import com.example.dweb_App.data.entities.*;
import com.example.dweb_App.data.service.InterventionService;
import com.example.dweb_App.data.service.NotificationService;
import com.example.dweb_App.data.service.TechnicianService;
import com.example.dweb_App.dto.request.NotificationRequestDTO;
import com.example.dweb_App.dto.response.NotificationResponseDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/notifications")

public class NotificationsController {

    private NotificationService notificationService;
    private TechnicianService technicianService;
    private InterventionService interventionService;

    public NotificationsController(NotificationService notificationService, TechnicianService technicianService, InterventionService interventionService) {
        this.notificationService = notificationService;
        this.technicianService = technicianService;
        this.interventionService = interventionService;
    }

    @PostMapping("/{email}")
    public ResponseEntity<?> sendNotificationToTechnician(@PathVariable String email, @RequestBody NotificationRequestDTO request){

        Technician technician=technicianService.loadTechnicianByEmail(email)
                .orElseThrow(()->new EntityNotFoundException("Technician Not Found"+email));

        Intervention intervention=interventionService.findInterventionById(request.getInterventionId())
                .orElseThrow(()->new EntityNotFoundException("Intervention Not Found"+request.getInterventionId()));

        String message;
        if(request.getType().equals(NotificationType.INTERVENTION_VALIDATED)) message= "Votre intervention avec " + request.getClientName() + " a été validée avec succès.";
        else message = "Votre intervention avec " + request.getClientName() + " a été rejetée.";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);

        Notifications notification=Notifications.builder()
                .technician(technician)
                .intervention(intervention)
                .message(message)
                .type(request.getType())
                .createdAt(formattedDateTime)
                .read(false).build();

        notificationService.saveNotification(notification);

        return ResponseEntity.ok("Notification is well sent");

    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getNotificationsOfTechnician(@PathVariable String email){

        Technician technician=technicianService.loadTechnicianByEmail(email)
                .orElseThrow(()->new EntityNotFoundException("Technician Not Found"+email));

        try {
            List<Notifications> notifications = notificationService.getAllNotifications();
            List<NotificationResponseDTO> notificationResponses = new ArrayList<>();

            for (Notifications notification : notifications) {

                String createdAt=notification.getCreatedAt();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
                LocalDateTime localDate = LocalDateTime.parse(createdAt, formatter);

                String duration = TimeUtils.formatTimeDifference(localDate);

                NotificationResponseDTO notificationResponse = NotificationResponseDTO.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .type(notification.getType())
                        .timestamp(duration)
                        .read(notification.getRead())
                        .interventionId(notification.getIntervention().getId())
                        .build();

                notificationResponses.add(notificationResponse);

            }

            return ResponseEntity.ok(notificationResponses);
        }catch (Exception e){
            throw e;
        }

    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<?> markNotifAsRead(@PathVariable Long notificationId){

        Notifications notification=notificationService.loadNotificationById(notificationId)
                .orElseThrow(()->new EntityNotFoundException("Notification Not Found "+notificationId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);

        notification.setRead(true);
        notification.setReadAt(formattedDateTime);

        notificationService.saveNotification(notification);

        return ResponseEntity.ok("Notification was read successfully");
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId){

        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification was deleted successfully");

    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllNotification(){

        notificationService.deleteAllNotifications();
        return ResponseEntity.ok("Notifications were deleted successfully");

    }
}
