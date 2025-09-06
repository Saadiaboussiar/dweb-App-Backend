package com.example.dweb_App.utils;

import com.example.dweb_App.dto.response.ChangePasswordResponse;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.repositories.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@Transactional
public class PasswordService {
    private static final String characters="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random=new SecureRandom();
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public PasswordService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public static String generatePassword(){
        StringBuilder password=new StringBuilder();

        for(int i=0;i<8;i++){
            int index=random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }

    public ChangePasswordResponse forcePasswordChange(String email, String currentPassword,String newPassword,String confirmPassword) {

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.isPasswordChangeRequired()) {
            throw new IllegalStateException("Password change not required");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);
        appUserRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, newPassword)
        );

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        ChangePasswordResponse changePasswordResponse= ChangePasswordResponse.builder()
                .technicianId(user.getId())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .message("Password changed successfully")
                .success(true)
                .build();


        return changePasswordResponse;
    }

    public boolean isPasswordChangeRequired(String email) {
        return appUserRepository.findByEmail(email)
                .map(AppUser::isPasswordChangeRequired)
                .orElse(false);
    }

}
