package com.example.dweb_App.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dweb_App.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;

    public JwtAuthorizationFilter(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/refreshToken") || request.getServletPath().equals("/login") || request.getServletPath().equals("/users")) {
            filterChain.doFilter(request, response);
            return;
        } else {

            String authorizationToken = request.getHeader("Authorization");

            if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
                try {
                    String jwt = authorizationToken.substring(7);
                    Algorithm algo = Algorithm.HMAC256("mySecret2005");
                    JWTVerifier jwtVerifier = JWT.require(algo).build();
                    DecodedJWT decodeJwt = jwtVerifier.verify(jwt);
                    String username = decodeJwt.getSubject();
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    String[] roles = decodeJwt.getClaim("roles").asArray(String.class);

                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    for (String r : roles) {
                        authorities.add(new SimpleGrantedAuthority(r));
                    }
                    ;
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    System.out.println("Token extracted: " + jwt);
                    System.out.println("Username from token: " + username);
                    System.out.println("roles for this user: " + authorities);

                } catch (TokenExpiredException ex) {

                    SecurityContextHolder.clearContext();
                    throw new AuthenticationServiceException("JWT token expired", ex);
                } catch (JWTVerificationException | IllegalArgumentException ex) {

                    SecurityContextHolder.clearContext();
                    throw new AuthenticationServiceException("Invalid JWT token", ex);
                } catch (Exception ex) {

                    SecurityContextHolder.clearContext();
                    logger.error("Unexpected error in JWT filter", ex);
                    throw new AuthenticationServiceException("Authentication failed", ex);
                }
            }
            filterChain.doFilter(request, response);

        }

    }
}

