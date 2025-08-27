package com.example.dweb_App.security.repositories;

import com.example.dweb_App.security.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole,Long> {
    Optional<AppRole> findByRoleName(String roleName);
}
