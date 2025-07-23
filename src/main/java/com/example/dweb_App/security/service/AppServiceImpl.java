package com.example.dweb_App.security.service;

import com.example.dweb_App.security.entities.AppRole;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.repositories.AppRoleRepository;
import com.example.dweb_App.security.repositories.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class AppServiceImpl implements AppService {
   private AppUserRepository appUserRepository;
   private AppRoleRepository appRoleRepository;
   private PasswordEncoder passwordEncoder;

    public AppServiceImpl(AppUserRepository appUserRepository, AppRoleRepository appRoleRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public AppUser addNewUser(AppUser appUser) {
        if(appUserRepository.findByUsername(appUser.getUsername())==null){
            String pw=appUser.getPassword();
            appUser.setPassword(passwordEncoder.encode(pw));
            return appUserRepository.save(appUser);
        }
        else{ return null; }
    }

    @Override
    public AppRole addNewRole(AppRole appRole) {
        if(appRoleRepository.findByRoleName(appRole.getRoleName())!=null){
            return appRoleRepository.save(appRole);
        }
        else{ return null; }
    }

    @Override
    public void addRoleToUser(String roleName, String username) {
        AppRole appRole= appRoleRepository.findByRoleName(roleName);
        AppUser appUser =appUserRepository.findByUsername(username);
        appUser.getUserRoles().add(appRole);
        appUserRepository.save(appUser);
    }

    @Override
    public AppUser loadUserBYUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> UsersList() {
        return appUserRepository.findAll();
    }
}
