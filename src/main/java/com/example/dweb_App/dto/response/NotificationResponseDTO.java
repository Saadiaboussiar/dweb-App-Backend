package com.example.dweb_App.dto.response;

import com.example.dweb_App.data.entities.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class NotificationResponseDTO {
    private Long id;
    private Long intervention_id;
    private String title;
    private String message;
    private String timestamp;
    private boolean isRead;
    private NotificationType type;
}
