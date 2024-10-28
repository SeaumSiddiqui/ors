package com.rms.ors.security;

import com.rms.ors.user.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class JwtUtil {
    private final TokenRepository tokenRepository;
    private final String secretKey = "aa02b0b42e8c2948f8dcafd2c251ee9cea3aff016f799a06d8669cc78a24405f";
    private final long jwtExpiration = 1000 * 60 * 60 * 24;
    private final long refreshExpiration = 1000 * 60 * 60 * 24 * 7;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public <T>T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private SecretKey getSignInKey() {
        byte[] bytes = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA256");
    }


    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, refreshExpiration);
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }


    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long tokenExpiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSignInKey())
                .compact();
    }


    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isTokenValid = tokenRepository
                .findByRefreshToken(token).map(t-> !t.isTokenRevoked()).orElse(false);
        return (username.equals(userDetails.getUsername()) && isTokenNonExpired(token) && isTokenValid);
    }


    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isTokenValid = tokenRepository
                .findByAccessToken(token).map(t-> !t.isTokenRevoked()).orElse(false);
        return (username.equals(userDetails.getUsername()) && isTokenNonExpired(token) && isTokenValid);
    }


    private boolean isTokenNonExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
