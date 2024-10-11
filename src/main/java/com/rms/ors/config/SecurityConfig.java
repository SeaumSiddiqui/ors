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

                        .requestMatchers("/users/**", "/dashboard/**").hasAnyRole(ADMIN.name())
                        
                        .requestMatchers("POST", "/users").hasAnyAuthority(ADMIN_CREATE.name())
                        .requestMatchers("GET", "/users/**", "/dashboard/**").hasAnyAuthority(ADMIN_READ.name())
                        .requestMatchers("PUT", "/users/**").hasAnyAuthority(ADMIN_UPDATE.name())
                        .requestMatchers("DELETE", "/users/**").hasAnyAuthority(ADMIN_DELETE.name())


                        .requestMatchers("/management/**").hasAnyRole(ADMIN.name(), MANAGEMENT.name())

                        .requestMatchers("GET", "/management**").hasAnyAuthority(ADMIN_READ.name(), MANAGEMENT_READ.name())
                        .requestMatchers("DELETE", "/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGEMENT_DELETE.name())


                        .requestMatchers("/user/**").hasAnyRole(ADMIN.name(), MANAGEMENT.name(), USER.name())

                        .requestMatchers("POST", "/user/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGEMENT_CREATE.name(), USER_CREATE.name())
                        .requestMatchers("GET", "/user/applications/{applicationId}").hasAnyAuthority(ADMIN_READ.name(), MANAGEMENT_READ.name(), USER_READ.name())
                        .requestMatchers("GET", "/user/**").hasAnyAuthority(USER_READ.name())
                        .requestMatchers("PUT", "/user/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGEMENT_UPDATE.name(), USER_UPDATE.name())

                        .requestMatchers("/self").hasAnyRole(ADMIN.name(), MANAGEMENT.name(), USER.name())
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
