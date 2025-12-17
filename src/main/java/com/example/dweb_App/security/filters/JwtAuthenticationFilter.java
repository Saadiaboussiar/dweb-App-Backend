package com.example.dweb_App.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.dweb_App.security.entities.AppRole;
import com.example.dweb_App.security.entities.AppUser;
import com.example.dweb_App.security.repositories.AppUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AppUserRepository userRepository;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AppUserRepository userRepository,
                                   ObjectMapper objectMapper) {
        super.setAuthenticationManager(authenticationManager);
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {System.out.println("attemptAuthentication");
       String email = request.getParameter("email");
       String password = request.getParameter("password");
       UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
       return getAuthenticationManager().authenticate(authenticationToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication");

        User user=(User) authResult.getPrincipal();

        boolean passwordChangeRequired = userRepository.findByEmail(user.getUsername())
                .map(AppUser::isPasswordChangeRequired)
                .orElse(false);

        Algorithm algorithm1=Algorithm.HMAC256("mySecret2005");
        List<String> rolesList=user.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList());

        String jwtAccessToken= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+30*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",rolesList) //On transforme une collection de type AppRole a une to type string
                .withClaim("pwdChangeRequired", passwordChangeRequired)
                .sign(algorithm1);

        String jwtRefreshToken= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+60*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("pwdChangeRequired", passwordChangeRequired)
                .sign(algorithm1);

        String role;
        if(rolesList.size()==2) role="ADMIN";
        else role="USER";

        Map<String,Object> idToken=new HashMap<>();

        idToken.put("access-token",jwtAccessToken);
        idToken.put("refresh-token",jwtRefreshToken);
        idToken.put("passwordChangeRequired", passwordChangeRequired);
        idToken.put("role",role);
        idToken.put("userEmail",user.getUsername());


        if (passwordChangeRequired) {
            idToken.put("redirect", "/change-password");
            idToken.put("message", "Password change required. Please update your password.");
        }

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(),idToken);

    }
}
