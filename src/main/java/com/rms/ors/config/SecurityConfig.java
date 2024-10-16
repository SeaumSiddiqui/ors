package com.rms.ors.config;

import com.rms.ors.security.CustomLogoutHandler;
import com.rms.ors.security.JwtAuthFilter;
import com.rms.ors.user.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.rms.ors.shared.Permission.*;
import static com.rms.ors.shared.Role.*;
import static org.springframework.http.HttpMethod.*;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomLogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request-> request
                        .requestMatchers("/auth/**").permitAll()

                        // Only ADMIN & MANAGEMENT Can Delete Applications
                        .requestMatchers("/delete/**").hasAnyRole(ADMIN.name(), MANAGEMENT.name())
                        .requestMatchers(DELETE, "/delete/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGEMENT_DELETE.name())

                        // Only ADMIN & USER Can Create Applications
                        .requestMatchers("/create").hasAnyRole(ADMIN.name(), USER.name())
                        .requestMatchers(POST, "/create").hasAnyAuthority(ADMIN_CREATE.name(), USER_CREATE.name())

                        // Applications Management
                        .requestMatchers("/applications/**").hasAnyRole(ADMIN.name(), MANAGEMENT.name(), USER.name())

                        .requestMatchers(GET, "/applications/**").hasAnyAuthority(ADMIN_READ.name(), MANAGEMENT_READ.name(), USER_READ.name())
                        .requestMatchers(PUT, "/applications/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGEMENT_UPDATE.name(), USER_UPDATE.name())


                        // User Management
                        .requestMatchers("/self").hasAnyRole(ADMIN.name(), MANAGEMENT.name(), USER.name())
                        .requestMatchers(GET, "/self").hasAnyAuthority(ADMIN_READ.name(), MANAGEMENT_READ.name(), USER_READ.name())

                        // ADMIN Only
                        .requestMatchers("/users/**", "/dashboard/**").hasRole(ADMIN.name())
                        
                        .requestMatchers(POST, "/users").hasAuthority(ADMIN_CREATE.name())
                        .requestMatchers(GET, "/users/**", "/dashboard/**").hasAuthority(ADMIN_READ.name())
                        .requestMatchers(PUT, "/users/**").hasAuthority(ADMIN_UPDATE.name())
                        .requestMatchers(DELETE, "/users/**").hasAuthority(ADMIN_DELETE.name())

                        .anyRequest()
                        .authenticated())
                .sessionManagement(manager->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(O_O-> O_O
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuditorAware<Long> applicationAuditAware() {
        return new ApplicationAuditAware();
    }

}
