package by.language.platform.config;

import by.language.platform.model.Role;
import by.language.platform.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                        // --- Документация ---
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/webjars/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // --- Авторизация ---
                        .requestMatchers("/auth/login").permitAll()

                        // --- Пользователи ---
                        .requestMatchers("/users").permitAll()

                        // --- Курсы ---
                        .requestMatchers("/courses/search").permitAll()
                        .requestMatchers("/courses/cheaper-than").permitAll()

                        // --- Покупки ---
                        .requestMatchers("/purchases/top-course").permitAll()

                        // --- Скидки ---
                        .requestMatchers("/discount-subscribers/exists").permitAll()
                        .requestMatchers("/discount-subscribers/count").permitAll()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        // Вызывается, когда нет аутентификации
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Authentication required\"}");
                        })
                        // Вызывается, когда есть аутентификация, но нет прав
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Access denied\"}");
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}