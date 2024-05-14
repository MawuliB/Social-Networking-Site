package com.mawuli.sns.repositories;

import com.mawuli.sns.security.domain.entities.Status;
import com.mawuli.sns.security.domain.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccessRepository extends CrudRepository<User, Long>, PagingAndSortingRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAllByStatus(Status status);
}
