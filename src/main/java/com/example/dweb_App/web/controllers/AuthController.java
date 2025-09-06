package com.example.dweb_App.web.controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dweb_App.dto.request.ChangePasswordRequest;
import com.example.dweb_App.dto.request.UserCreateDTO;
import com.example.dweb_App.dto.response.ChangePasswordResponse;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.security.entities.AppRole;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import com.example.dweb_App.utils.PasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AuthController {

    private AppService appService;
    private PasswordService passwordService;
    private AuthenticationManager authenticationManager;

    public AuthController(AppService appService, PasswordService passwordService, AuthenticationManager authenticationManager) {
        this.appService = appService;
        this.passwordService = passwordService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/users")
    public ResponseEntity<?> addUsers(@RequestBody UserCreateDTO user) {
        log.info("Received user: {}", user);

        AppUser newUser= AppUser.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .userRoles(new ArrayList<>()).build();

        AppUser savedUser=appService.addNewUser(newUser);

        return ResponseEntity.ok(Map.of(
                "roles", appService.getRolesOfUser(newUser),
                "email", savedUser.getEmail(),
                "username", savedUser.getUsername()));

    }

    @PostMapping("/refreshToken")
    public void refreshToken(@RequestBody Map<String, String> tokenMap, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String refreshToken = tokenMap.get("refreshToken");
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            try {
                String jwt = refreshToken.substring(7);
                Algorithm algo = Algorithm.HMAC256("mySecret2005");
                JWTVerifier jwtVerifier = JWT.require(algo).build();
                DecodedJWT decodeJwt = jwtVerifier.verify(jwt);
                String email = decodeJwt.getSubject();
                System.out.println("Utilisateur accéder au refreshToken: "+email);
                AppUser appUser = appService.loadUserBYEmail(email)
                        .orElseThrow(()->new EntityNotFoundException("Utilisateur non trouvé"));
                String jwtAccessToken = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", appUser.getUserRoles().stream()
                                .map(AppRole::getRoleName)
                                .collect(Collectors.toList()))
                        .sign(algo);

                Map<String, String> idToken = new HashMap<>();
                idToken.put("access-token", jwtAccessToken);
                idToken.put("refresh-token", jwt);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), idToken);
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new RuntimeException("Refresh Token required!!");
        }
    }



    @GetMapping("/auth/password-change-required")
    public ResponseEntity<Boolean> isPasswordChangeRequired(Principal principal) {
        boolean required = passwordService.isPasswordChangeRequired(principal.getName());
        return ResponseEntity.ok(required);
    }



    @PostMapping("/auth/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                            @RequestHeader("Authorization") String authHeader) {
        // Extract token and process password change
        String token = authHeader.substring(7);
        Algorithm algo = Algorithm.HMAC256("mySecret2005");
        JWTVerifier jwtVerifier = JWT.require(algo).build();
        DecodedJWT decodeJwt = jwtVerifier.verify(token);
        String username = decodeJwt.getSubject();

        // Your password change logic here
        ChangePasswordResponse response =passwordService.forcePasswordChange(username, request.getCurrentPassword(), request.getNewPassword(),request.getConfirmPassword());

        return ResponseEntity.ok(response);
    }

}

