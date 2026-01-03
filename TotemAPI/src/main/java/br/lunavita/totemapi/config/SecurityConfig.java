package br.lunavita.totemapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint((request, response, authException) -> {
                        System.out.println("[SECURITY] Unauthorized access to: " + request.getRequestURI() + " - "
                                + authException.getMessage());
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Unauthorized - Token from LunaCore required\"}");
                    });
                })
                .authorizeHttpRequests(auth -> auth
                        // Allow all OPTIONS requests (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        // Public health/actuator
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                        
                        // Webhooks (Resend email notifications, etc)
                        .requestMatchers("/api/webhooks/**").permitAll()
                        
                        // Payment endpoints (returns 410 Gone - deprecated)
                        .requestMatchers("/api/payments/**").permitAll()
                        
                        // ===== PUBLIC TOTEM ENDPOINTS (no auth required) =====
                        // Check-in by CPF (public totem kiosk)
                        .requestMatchers(HttpMethod.GET, "/api/patients/cpf/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/appointments/patient/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/appointments/upcoming").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/appointments/*/checkin").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/*/status").permitAll()
                        
                        // Dashboard summary (public, sanitized data)
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/summary").permitAll()
                        
                        // Doctors list (for appointment display)
                        .requestMatchers(HttpMethod.GET, "/api/doctors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/doctors/**").permitAll()
                        
                        // Videos for waiting room
                        .requestMatchers(HttpMethod.GET, "/api/videos/**").permitAll()
                        
                        // ===== AUTHENTICATED ENDPOINTS (admin panel) =====
                        // All other endpoints require authentication (JWT from LunaCore)
                        .anyRequest().authenticated())
                        
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
