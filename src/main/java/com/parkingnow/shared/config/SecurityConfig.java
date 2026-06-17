package com.parkingnow.shared.config;

import com.parkingnow.shared.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    antMatcher("/"),
                    antMatcher("/health"),
                    antMatcher("/actuator/health"),
                    antMatcher("/h2-console/**"),
                    antMatcher("/swagger-ui/**"),
                    antMatcher("/swagger-ui.html"),
                    antMatcher("/swagger-ui/index.html"),
                    antMatcher("/v3/api-docs/**"),
                    antMatcher("/webjars/**"),
                    antMatcher("/api/v1/auth/sign-in"),
                    antMatcher("/api/v1/auth/sign-up/**"),
                    antMatcher("/api/v1/iot/events"),
                    antMatcher("/api/v1/iot/heartbeat"),
                    antMatcher("/api/v1/iot/nodes/**"),
                    antMatcher("/api/v1/camera/snapshot/**"),
                    antMatcher("/api/v1/spaces/parking-lot/**")
                ).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/parking-lots", "GET"),
                                 new AntPathRequestMatcher("/api/v1/parking-lots/*", "GET")).permitAll()
                .anyRequest().authenticated()
            )
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
