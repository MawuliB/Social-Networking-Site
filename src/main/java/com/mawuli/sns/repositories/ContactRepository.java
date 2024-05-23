package com.mawuli.sns.repositories;

import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.security.domain.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends CrudRepository<Contact, Integer> {
    List<Contact> findAllByUser(User user);
    List<Contact> findAllByUserAndIsAcceptedTrueAndIsBlacklistedFalse(User user);

    Optional<Contact> findByUserAndContact(User user, User contactUser);

    List<Contact> findAllByContactAndIsAcceptedFalse(User user);

}
