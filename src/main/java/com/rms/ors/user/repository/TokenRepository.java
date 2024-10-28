package com.rms.ors.user.repository;

import com.rms.ors.user.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
            SELECT t FROM Token t
            INNER JOIN User u ON t.user.id = u.id
            WHERE t.user.id = :userId AND t.tokenRevoked = false
            """)
    List<Token> findAllTokenByUserId (Long userId);

    Optional<Token> findByAccessToken(String token);

    Optional<Token> findByRefreshToken(String token);

}
