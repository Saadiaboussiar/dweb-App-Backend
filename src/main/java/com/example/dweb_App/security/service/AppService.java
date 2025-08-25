package com.example.dweb_App.security.service;

import com.example.dweb_App.security.entities.AppRole;
import com.example.dweb_App.security.entities.AppUser;

import java.util.Collection;
import java.util.List;

public interface AppService {
    AppUser addNewUser(AppUser appUser);
    AppRole addNewRole(AppRole appRole);
    void addRoleToUser(String roleName, String email);
    AppUser loadUserBYEmail(String email);
    List<AppUser> UsersList();
    Collection<String> getRolesOfUser(AppUser user);
}
