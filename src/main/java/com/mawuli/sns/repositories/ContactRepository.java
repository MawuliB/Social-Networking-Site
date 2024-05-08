package com.mawuli.sns.repositories;

import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.security.domain.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends CrudRepository<Contact, Integer>, PagingAndSortingRepository<Contact, Integer> {
    public List<Contact> findAllByUser(User user);

    Optional<Contact> findByUserAndContact(User user, User contactUser);

    List<Contact> findAllByContactAndIsAcceptedFalse(User user);
}
