package com.mawuli.sns.security.repositories;

import com.mawuli.sns.security.domain.user.RefreshToken;
import com.mawuli.sns.security.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);
    void deleteByToken(String token);

    RefreshToken findByUser(User user);
}
