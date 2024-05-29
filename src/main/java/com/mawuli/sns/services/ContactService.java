package com.mawuli.sns.services;

import com.mawuli.sns.domain.dto.mappers.ContactMapper;
import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.exceptionhandler.graphql.EntityNotFoundException;
import com.mawuli.sns.exceptionhandler.graphql.GeneralGraphQLExceptions;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.repositories.UserAccessRepository;
import com.mawuli.sns.security.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserAccessRepository userAccessRepository;
    private final ContactMapper contactMapper;

    public Contact addContactToContactList(ContactDto contactDto) {
        var contactEntity = contactMapper.mapToContact(contactDto);
        User user = contactEntity.getUser();
        User contactUser = contactEntity.getContact();

        if(user.getId().equals(contactUser.getId())){
            throw new GeneralGraphQLExceptions("You cannot add yourself as a contact");
        }

        Optional<Contact> existingContact = contactRepository.findByUserAndContact(user, contactUser);
        Optional<Contact> existingContactReverse = contactRepository.findByUserAndContact(contactUser, user);

        if (existingContact.isPresent()) {
            if (existingContact.get().getIsBlacklisted()) {
                throw new GeneralGraphQLExceptions("Contact exists, not allowing connection");
            } else {
                throw new GeneralGraphQLExceptions("Contact already exists");
            }
        }

        if (existingContactReverse.isPresent()) {
            if (existingContactReverse.get().getIsAccepted()){
                contactEntity.setIsAccepted(true);
                contactRepository.save(contactEntity);
                throw new GeneralGraphQLExceptions("Contact already your friend");
            } else {
                existingContactReverse.get().setIsAccepted(true);
                return contactRepository.save(existingContactReverse.get());
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

    public void acceptContactInvitation(Integer contactId) {
        var contact = contactRepository.findById(contactId);

        if (contact.isPresent()) {
            contact.get().setIsAccepted(true);
            contactRepository.save(contact.get());
            //create a new contact for the other user
            var newContact = new Contact();
            newContact.setUser(contact.get().getContact());
            newContact.setContact(contact.get().getUser());
            newContact.setIsBlacklisted(false);
            newContact.setIsAccepted(true);
            contactRepository.save(newContact);
        } else {
            throw new EntityNotFoundException("Contact does not exist");
        }
    }

    public List<Contact> getAllContactByContactId(Long aLong) {
        return contactRepository.findAllByUserId(aLong);
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

    public List<Contact> getInvitationsByUserId(Long userId) {
        return contactRepository.findAllByContactIdAndIsAcceptedFalse(userId);
    }
}
