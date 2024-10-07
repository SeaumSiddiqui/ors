package com.rms.ors.config;

import com.rms.ors.security.CustomLogoutHandler;
import com.rms.ors.security.JwtAuthFilter;
import com.rms.ors.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import static com.rms.ors.domain.Permission.*;
import static com.rms.ors.domain.Role.*;

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
                        .requestMatchers("/login")
                        .permitAll()
                        .requestMatchers("/users/**", "/admin/**").hasAnyRole(ADMIN.name())
                        
                        .requestMatchers("POST", "/users").hasAnyAuthority(ADMIN_CREATE.name())
                        .requestMatchers("GET", "/users/**").hasAnyAuthority(ADMIN_READ.name())
                        .requestMatchers("PUT", "/users/**").hasAnyAuthority(ADMIN_UPDATE.name())
                        .requestMatchers("DELETE", "/users/**").hasAnyAuthority(ADMIN_DELETE_USER.name())
                        .requestMatchers("DELETE", "/admin/**").hasAnyAuthority(ADMIN_DELETE_APPLICATION.name())


                        .requestMatchers("/management/**").hasAnyRole(ADMIN.name(), MANAGEMENT.name())

                        .requestMatchers("PUT", "/management**").hasAnyAuthority(ADMIN_UPDATE.name(),MANAGEMENT_UPDATE.name())
                        .requestMatchers("GET", "/management**").hasAnyAuthority(ADMIN_READ.name(),MANAGEMENT_READ.name())


                        .requestMatchers("/user/**").hasAnyRole(ADMIN.name(), USER.name())

                        .requestMatchers("POST", "/user/**").hasAnyAuthority(ADMIN_CREATE.name(), USER_CREATE.name())
                        .requestMatchers("GET", "/user/**").hasAnyAuthority(USER_READ.name())
                        .requestMatchers("PUT", "/user/**").hasAnyAuthority(USER_READ.name())

                        .requestMatchers("/self/**").hasAnyRole(ADMIN.name(), MANAGEMENT.name(), USER.name())
                        .anyRequest()
                        .authenticated())
                .sessionManagement(manager->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(l-> l
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

}
