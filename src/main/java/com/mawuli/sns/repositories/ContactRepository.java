package com.mawuli.sns.repositories;

import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.security.domain.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends CrudRepository<Contact, Integer> {
    List<Contact> findAllByUser(User user);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    List<Contact> findAllByUserId(@Param("userId") Long userId);

    List<Contact> findAllByUserAndIsAcceptedTrueAndIsBlacklistedFalse(User user);

    Optional<Contact> findByUserAndContact(User user, User contactUser);

    List<Contact> findAllByContactAndIsAcceptedFalse(User user);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.isAccepted = true AND c.isBlacklisted = false")
    List<Contact> findAllByUserIdAndIsAcceptedTrueAndIsBlacklistedFalse(@Param("userId") Long userId);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.contact.id = :contactId")
    Optional<Contact> findByUserIdAndContactId(@Param("userId") Long userId, @Param("contactId") Long contactId);

    @Query("SELECT c FROM Contact c WHERE c.contact.id = :userId AND c.isAccepted = false")
    List<Contact> findAllByContactIdAndIsAcceptedFalse(@Param("userId") Long userId);

}
