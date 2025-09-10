package com.example.dweb_App.data.service;

import com.example.dweb_App.dto.response.UserProfileDTO;

public interface AdminService {
    UserProfileDTO getProfileData(String email);
}
