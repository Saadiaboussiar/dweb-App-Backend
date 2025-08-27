package com.example.dweb_App.security.service;

import com.example.dweb_App.exception.BusinessException;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.security.entities.AppRole;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.repositories.AppRoleRepository;
import com.example.dweb_App.security.repositories.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if(appUserRepository.existsByEmail(appUser.getEmail())){
            throw new BusinessException(
                    "Email Exist Déjà",
                    "Un utilisateur avec cette adresse e-mail exist déjà",
                    "e-mail"
            );
        }
        if (appUserRepository.existsByUsername(appUser.getUsername())){
            throw new BusinessException(
                    "Nom d'utilisateur Exist Déjà",
                    "Un utilisateur avec ce nom d'utilisateur exist déjà",
                    "Nom d'utilisateur"
            );
        }
        String pw=appUser.getPassword();
        appUser.setPassword(passwordEncoder.encode(pw));
        return appUserRepository.save(appUser);

    }

    @Override
    public AppRole addNewRole(AppRole appRole) {
        if(appRoleRepository.findByRoleName(appRole.getRoleName())!=null){
            return appRoleRepository.save(appRole);
        }
        else{ return null; }
    }

    @Override
    public void addRoleToUser(String roleName, String email) {
        AppRole appRole= appRoleRepository.findByRoleName(roleName)
                .orElseThrow(()->new EntityNotFoundException("Ce role n'existe pas"));
        AppUser appUser =appUserRepository.findByEmail(email)
                .orElseThrow(()->new EntityNotFoundException("Utiliateur avec cette adresse e-mail n'existe pas"));
        appUser.getUserRoles().add(appRole);
        appUserRepository.save(appUser);
    }

    @Override
    public Optional<AppUser> loadUserBYEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public Optional<AppUser> loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> UsersList() {
        return appUserRepository.findAll();
    }

    @Override
    public Collection<String> getRolesOfUser(AppUser user) {
        Collection<AppRole> roles=user.getUserRoles();
        List<String> rolesNames=roles.stream()
                                    .map(role->role.getRoleName())
                                    .collect(Collectors.toList());
        return rolesNames;
    }


}
