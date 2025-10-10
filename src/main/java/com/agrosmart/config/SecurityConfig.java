package com.agrosmart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123")
                .roles("ADMIN")
                .build();
        UserDetails user = User.withUsername("user")
                .password("{noop}user123")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/ping", "/error").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/alerts/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/purchase-orders/**").hasAnyRole("USER","ADMIN")

                        // Todo lo que sea mutación bajo /api/** solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );
        return http.build();
    }

}
