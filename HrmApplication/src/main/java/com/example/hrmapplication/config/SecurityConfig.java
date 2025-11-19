package com.example.hrmapplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Public resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        // Admin routes - chỉ ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // HR routes - ADMIN và HR
                        .requestMatchers("/hr/**").hasAnyRole("ADMIN", "HR")
                        // Manager routes - ADMIN, HR, MANAGER
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                        // Employee routes - tất cả đã đăng nhập
                        .requestMatchers("/employee/**", "/my/**").authenticated()
                        // Dashboard - tất cả đã đăng nhập
                        .requestMatchers("/dashboard/**", "/").authenticated()
                        // API endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/hr/**").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/api/employee/**").authenticated()
                        // Các route còn lại yêu cầu đăng nhập
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/keycloak")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(authoritiesMapper())
                        )
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                );

        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper authoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            
            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    Map<String, Object> userAttributes = oidcUserAuthority.getAttributes();
                    
                    // Lấy roles từ realm_access
                    Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get("realm_access");
                    if (realmAccess != null) {
                        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                        if (roles != null) {
                            roles.forEach(role -> {
                                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                            });
                        }
                    }
                } else if (authority instanceof OAuth2UserAuthority) {
                    OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
                    Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
                    
                    // Lấy roles từ realm_access
                    Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get("realm_access");
                    if (realmAccess != null) {
                        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                        if (roles != null) {
                            roles.forEach(role -> {
                                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                            });
                        }
                    }
                }
            });
            
            return mappedAuthorities;
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) {
                return java.util.Collections.emptyList();
            }
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null) {
                return java.util.Collections.emptyList();
            }

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return converter;
    }
}