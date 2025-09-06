package com.example.dweb_App.security.filters;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dweb_App.security.repositories.AppUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PasswordChangeCheckFilter extends OncePerRequestFilter {

    private final AppUserRepository appUserRepository;
    private final ObjectMapper objectMapper;

    public PasswordChangeCheckFilter(AppUserRepository userRepository, ObjectMapper objectMapper) {
        this.appUserRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Skip check for authentication and password change endpoints
        if (isExcludedEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get authentication from security context
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            boolean passwordChangeRequired = appUserRepository.findByEmail(username)
                    .map(user -> user.isPasswordChangeRequired())
                    .orElse(false);

            if (passwordChangeRequired) {
                sendPasswordChangeRequiredResponse(response);
                return; // Stop further processing
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcludedEndpoint(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.equals("/auth/login") ||
                requestUri.equals("/auth/change-password") ||
                requestUri.equals("/refreshToken") ||
                requestUri.startsWith("/public/");
    }

    private void sendPasswordChangeRequiredResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Password change required");
        responseBody.put("message", "You must change your password before accessing this resource");
        responseBody.put("redirect", "/change-password");

        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}
