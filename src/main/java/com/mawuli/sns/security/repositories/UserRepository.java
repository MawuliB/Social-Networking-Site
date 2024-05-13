package com.mawuli.sns.security.repositories;

import com.mawuli.sns.security.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
