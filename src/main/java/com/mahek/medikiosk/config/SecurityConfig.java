
package com.mahek.medikiosk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Temporary configuration for the Postman API prototype.
                // Revisit CSRF protection before browser-based deployment.
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/clinical-summaries/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(
                                "/api/medical-histories/**",
                                "/api/medical-documents/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "STAFF")

                        .requestMatchers(HttpMethod.DELETE, "/api/patients/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/patients/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "STAFF")

                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            @Value("${MEDIKIOSK_ADMIN_PASSWORD}")
            String adminPassword,
            @Value("${MEDIKIOSK_DOCTOR_PASSWORD}")
            String doctorPassword,
            @Value("${MEDIKIOSK_STAFF_PASSWORD}")
            String staffPassword) {

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        UserDetails doctor = User.withUsername("doctor")
                .password(encoder.encode(doctorPassword))
                .roles("DOCTOR")
                .build();

        UserDetails staff = User.withUsername("staff")
                .password(encoder.encode(staffPassword))
                .roles("STAFF")
                .build();

        return new InMemoryUserDetailsManager(
                admin, doctor, staff);
    }
}
