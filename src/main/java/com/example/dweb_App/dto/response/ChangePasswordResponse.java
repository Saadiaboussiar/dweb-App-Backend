package com.example.dweb_App.dto.response;

import lombok.Builder;
import lombok.Data;

@Data  @Builder
public class ChangePasswordResponse {
    private Long technicianId;
    private String accessToken;
    private String refreshToken;
    private String message;
    private boolean success;
}
