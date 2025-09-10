package com.example.dweb_App.utils;

import com.example.dweb_App.data.entities.Admins;
import com.example.dweb_App.data.entities.Technician;
import com.example.dweb_App.data.repositories.AdminRepository;
import com.example.dweb_App.data.repositories.TechnicianRepository;
import com.example.dweb_App.dto.response.UserProfileDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private AdminRepository adminRepository;
    private TechnicianRepository technicianRepository;

    public ProfileService(AdminRepository adminRepository, TechnicianRepository technicianRepository) {
        this.adminRepository = adminRepository;
        this.technicianRepository = technicianRepository;
    }

    public UserProfileDTO getProfileData(String email) {
        // Check if the email belongs to an admin
        Optional<Admins> admin = adminRepository.findByEmailAndActiveTrue(email);
        if (admin.isPresent()) {
            return buildAdminProfile(admin.get());
        }

        // Check if the email belongs to a technician
        Optional<Technician> technician = technicianRepository.findByEmail(email);
        if (technician.isPresent()) {
            return buildTechnicianProfile(technician.get());
        }

        throw new EntityNotFoundException("User not found with email: " + email);
    }

    private UserProfileDTO buildAdminProfile(Admins admin) {
        return UserProfileDTO.builder()
                .email(admin.getEmail())
                .fullName(admin.getUsername())
                .profileUrl(admin.getProfileUrl())
                .role("ADMIN")
                .cin(null)
                .build();
    }

    private UserProfileDTO buildTechnicianProfile(Technician technician) {
        return UserProfileDTO.builder()
                .email(technician.getEmail())
                .fullName(technician.getFirstName()+" "+technician.getLastName())
                .profileUrl(technician.getPhotoUrl())
                .role("TECHNICIAN")
                .phoneNumber(technician.getPhoneNumber())
                .cin(technician.getCin())
                .build();
    }

    public String saveProfilePhoto(MultipartFile file, String folderName) throws IOException {
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String projectRoot = System.getProperty("user.dir");
        Path uploadDir = Paths.get(projectRoot, "uploads", folderName);

        // Create directory if it doesn't exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(filename);
        file.transferTo(filePath.toFile());

        return filename;
    }
}
