package com.example.dweb_App.security;

import com.example.dweb_App.security.filters.JwtAuthenticationFilter;
import com.example.dweb_App.security.filters.JwtAuthorizationFilter;
import com.example.dweb_App.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,AuthenticationManager authenticationManager) throws Exception{

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/test").permitAll()
                        .requestMatchers("/technicianProfile").authenticated()
                        .requestMatchers(HttpMethod.GET, "/technicianProfile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/technicianProfile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/technicianProfile").authenticated()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/bonIntervention").permitAll()
                        .anyRequest().authenticated()
                );
        //disable frames for 2-console:
        http.headers(headers ->
                headers.frameOptions(frame->frame.sameOrigin()));

        //add filters:
        JwtAuthenticationFilter jwtAuthenticationFilter=new JwtAuthenticationFilter(authenticationManager);
        JwtAuthorizationFilter jwtAuthorizationFilter=new JwtAuthorizationFilter(userDetailsService);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        http
                .addFilter(jwtAuthenticationFilter)
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");


            }
        };
    }





}
