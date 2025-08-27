package com.example.dweb_App.web;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dweb_App.dto.request.UserCreateDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.security.entities.AppRole;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.service.AppService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AppController {
    private AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;

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
        appService.addRoleToUser("USER", savedUser.getEmail());

        return ResponseEntity.ok(Map.of(
                "roles", appService.getRolesOfUser(newUser),
                "email", user.getEmail(),
                "username", user.getUsername()));

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
}

