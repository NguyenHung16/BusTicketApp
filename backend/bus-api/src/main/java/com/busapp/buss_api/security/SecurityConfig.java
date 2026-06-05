package com.busapp.buss_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth

                // ===== Public =====
                .requestMatchers(
                    "/api/auth/**",
                    "/api/public/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/error"
                ).permitAll()

                // ===== Public GET =====
                .requestMatchers(HttpMethod.GET,
                    "/api/provinces/**",
                    "/api/vehicle-types/**",
                    "/api/routes/**",
                    "/api/trips/**",
                    "/api/operators/**",
                    "/api/seats/**",
                    "/api/bookings/code/**"
                ).permitAll()

                // ===== OPERATOR + ADMIN =====
                .requestMatchers(HttpMethod.POST, "/api/operators/**").hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/operators/**").hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/trips/**").hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/trips/**").hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/routes/**").hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/routes/**").hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/points/**").hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/points/**").hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/bookings/*/confirm").hasAnyRole("OPERATOR", "ADMIN")

                // ===== ADMIN only =====
                .requestMatchers(HttpMethod.DELETE,
                    "/api/operators/**",
                    "/api/users/**",
                    "/api/trips/**",
                    "/api/routes/**",
                    "/api/points/**",
                    "/api/vehicle-types/**"
                ).hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/vehicle-types/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/vehicle-types/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/payment").hasRole("ADMIN")

                // ===== Authenticated =====
                .requestMatchers("/api/bookings").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/bookings/*/cancel").authenticated()
                .requestMatchers("/api/reviews/**").authenticated()
                .requestMatchers("/api/seats/lock/**").authenticated()

                // ===== Default =====
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
