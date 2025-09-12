package com.example.dweb_App.dto.request;

import com.example.dweb_App.data.entities.NotificationType;
import lombok.Data;

@Data
public class NotificationRequestDTO {
    private String clientName;
    private Long interventionId;
    private NotificationType type;
}
