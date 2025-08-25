package com.example.dweb_App.security.service;

import com.example.dweb_App.security.entities.AppRole;
import com.example.dweb_App.security.entities.AppUser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppService {
    AppUser addNewUser(AppUser appUser);
    AppRole addNewRole(AppRole appRole);
    void addRoleToUser(String roleName, String email);
    Optional<AppUser> loadUserBYEmail(String email);
    Optional<AppUser> loadUserByUsername(String username);
    List<AppUser> UsersList();
    Collection<String> getRolesOfUser(AppUser user);
}
