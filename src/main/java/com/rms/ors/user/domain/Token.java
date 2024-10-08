package com.rms.ors.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accessToken;
    private String refreshToken;
    private boolean loggedOut;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
