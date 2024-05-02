package com.mawuli.sns.services;

import com.mawuli.sns.domain.dto.mappers.ContactMapper;
import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.exceptionhandler.graphql.EntityNotFoundException;
import com.mawuli.sns.exceptionhandler.graphql.GeneralGraphQLExceptions;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.repositories.UserAccessRepository;
import com.mawuli.sns.security.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
    private final ContactRepository contactRepository;
    private final UserAccessRepository userAccessRepository;
    private final ContactMapper contactMapper;

    public Contact addContactToContactList(ContactDto contactDto) {
        var contactEntity = contactMapper.mapToContact(contactDto);
        User user = contactEntity.getUser();
        User contactUser = contactEntity.getContact();

        Optional<Contact> existingContact = contactRepository.findByUserAndContact(user, contactUser);

        if (existingContact.isPresent()) {
            if (existingContact.get().getIsBlacklisted()) {
                throw new GeneralGraphQLExceptions("Contact exists, not allowing connection");
            } else {
                throw new GeneralGraphQLExceptions("Contact already exists");
            }
        }

        return contactRepository.save(contactEntity);
    }

    public void removeContactFromContactList(Integer contactId) {
        var contact = contactRepository.findById(contactId);

        if (contact.isPresent() && !contact.get().getIsBlacklisted()) {
            contactRepository.delete(contact.get());
        } else {
            throw new EntityNotFoundException("Contact does not exist or is blacklisted");
        }
    }

    public Contact acceptContactInvitation(Integer contactId) {
        var contact = contactRepository.findById(contactId);

        if (contact.isPresent()) {
            contact.get().setIsAccepted(true);
            return contactRepository.save(contact.get());
        } else {
            throw new EntityNotFoundException("Contact does not exist");
        }
    }

    public List<Contact> getAllContactByContactId(Long aLong) {
        return contactRepository.findAllByUser(userAccessRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + aLong))
        );
    }

    public Contact addContactToBlackList(Integer contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + contactId));
        contact.setIsBlacklisted(true);
        return contactRepository.save(contact);
    }

    public Contact removeContactFromBlackList(Integer contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + contactId));
        contact.setIsBlacklisted(false);
        contactRepository.save(contact);
        return contact;
    }
}
