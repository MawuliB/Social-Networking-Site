package com.mawuli.sns.security.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime validatedAt;
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
