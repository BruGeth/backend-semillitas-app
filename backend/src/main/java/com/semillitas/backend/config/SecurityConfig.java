package com.semillitas.backend.config;

import com.semillitas.backend.security.JwtFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

  /** Evita que Spring cree el usuario por defecto con contraseña en el log. */
  @Bean
  UserDetailsService userDetailsService() {
    return dni -> { throw new UsernameNotFoundException("no-op"); };
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
    return http
        .cors(c -> {})
        .csrf(csrf -> csrf.disable()) // API stateless con JWT en header (sin cookies)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .headers(h -> h
            .frameOptions(f -> f.deny())
            .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)))
        .authorizeHttpRequests(a -> a
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/auth/login", "/actuator/health", "/actuator/metrics").permitAll()
            .requestMatchers("/api/matriculas/**").hasRole("DIRECTORA")
            .requestMatchers("/api/auditoria/**").hasRole("DIRECTORA")
            .requestMatchers("/api/salones/**").hasAnyRole("DIRECTORA", "DOCENTE")
            .requestMatchers("/api/asistencias/**").hasAnyRole("DIRECTORA", "DOCENTE")
            .requestMatchers("/api/salud/**").hasAnyRole("DIRECTORA", "DOCENTE")
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") List<String> origins) {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(origins);
    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    cfg.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return src;
  }
}
