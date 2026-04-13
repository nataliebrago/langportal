package by.language.platform.config;

import by.language.platform.model.Role;
import by.language.platform.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static by.language.platform.model.Role.ADMIN;

@Configuration
@EnableWebSecurity
public class JWTSecurityConfig {

    private final JwtFilter jwtFilter;

    public JWTSecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // --- Публичные ---
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/webjars/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/auth/login").permitAll()

                        // --- Курсы ---
                        .requestMatchers("/courses/search", "/courses/cheaper-than").permitAll()
                        .requestMatchers("/courses").hasRole("ADMIN")
                        .requestMatchers("/courses/**").hasRole("ADMIN")

                        // --- Пользователи ---
                       // .requestMatchers("/users").hasRole("ADMIN")

                        .requestMatchers("/users").permitAll()
                        .requestMatchers("/users/confirmed").hasRole(Role.ADMIN.name())
                        .requestMatchers("/users/{id}/password").hasAnyRole(Role.ADMIN.name(), Role.USER.name())

                        // --- Покупки ---
                        .requestMatchers("/purchases/check").permitAll()
                        .requestMatchers("/purchases/history").hasAnyRole(Role.ADMIN.name(),Role.USER.name())
                        .requestMatchers("/purchases/top-course").permitAll()
                        .requestMatchers("/purchases").hasAnyRole(Role.ADMIN.name(), Role.USER.name())

                        // --- Скидки ---
                        .requestMatchers("/discount-subscribers/exists").permitAll()
                        .requestMatchers("/discount-subscribers/count").permitAll()
                        .requestMatchers("/discount-subscribers").hasAnyRole(Role.ADMIN.name(), Role.USER.name())
                        .requestMatchers("/discount-subscribers/by-email").hasAnyRole(Role.ADMIN.name(),Role.USER.name())

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}