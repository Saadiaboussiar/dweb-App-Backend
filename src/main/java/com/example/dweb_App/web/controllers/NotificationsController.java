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
import java.util.ArrayList;
import java.util.List;

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
        if(request.getType().equals(NotificationType.INTERVENTION_VALIDATED)) message= "Votre intervention avec #" + request.getClientName() + " a été validée avec succès.";
        else message = "Votre intervention avec #" + request.getClientName() + " a été rejetée.";

        Notifications notification=Notifications.builder()
                .technician(technician)
                .intervention(intervention)
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false).build();

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

                String duration = TimeUtils.formatTimeDifference(notification.getCreatedAt());

                NotificationResponseDTO notificationResponse = NotificationResponseDTO.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .type(notification.getType())
                        .timestamp(duration)
                        .isRead(false)
                        .intervention_id(notification.getIntervention().getId())
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

        notification.setReadAt(LocalDateTime.now());

        notificationService.saveNotification(notification);

        return ResponseEntity.ok("Notification was read successfully");
    }
}
