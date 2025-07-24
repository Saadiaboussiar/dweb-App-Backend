package com.example.dweb_App.security.service;

import com.example.dweb_App.security.entities.AppRole;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.repositories.AppUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser=appUserRepository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Collection<AppRole> roles = appUser.getUserRoles();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(r -> {
            authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
        });
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                authorities);

    }
}
