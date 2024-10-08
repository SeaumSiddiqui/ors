package com.rms.ors.security;

import com.rms.ors.user.domain.Token;
import com.rms.ors.user.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwtToken = authHeader.substring(7);
        Token storedToken = tokenRepository.findByAccessToken(jwtToken).orElse(null);

        if (storedToken != null) {
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
        }
    }

}
