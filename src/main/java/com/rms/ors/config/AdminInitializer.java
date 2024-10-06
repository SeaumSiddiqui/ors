package com.rms.ors.config;

import com.rms.ors.domain.Role;
import com.rms.ors.domain.User;
import com.rms.ors.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        if(userRepository.count() == 0) {
            userRepository.save(
                User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("adminPassword"))
                        .role(Role.ADMIN).build()
            );
        }
    }
}
