package com.rms.ors.security;

import com.rms.ors.user.domain.Token;
import com.rms.ors.user.domain.User;
import com.rms.ors.user.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.List;

// TODO-> annotate with @Transactional
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
        tokenRepository.findByAccessToken(jwtToken).ifPresent(
                storedToken -> revokeAllTokenByUser(storedToken.getUser())
        );
    }


    private void revokeAllTokenByUser(User user) {
        List<Token> tokensByUser = tokenRepository.findAllTokenByUserId(user.getId());

        if(tokensByUser.isEmpty()) {
            return;
        }
        // Revoke access tokens only
        tokensByUser.stream()
                .filter(t-> !t.isTokenRevoked())
                .forEach(t-> t.setTokenRevoked(true));

        tokenRepository.saveAll(tokensByUser);
        tokenRepository.deleteAll(tokensByUser);
    }

}
