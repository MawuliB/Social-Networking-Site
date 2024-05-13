package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.exceptionhandler.graphql.EntityNotFoundException;
import com.mawuli.sns.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactMapper {

    private final UserService userService;

    public ContactDto mapToContactDto(Contact contact) {
        var user = contact.getUser().getId();
        var contactId = contact.getContact().getId();
        var isAccepted = contact.getIsAccepted();
        var isBlacklisted = contact.getIsBlacklisted();

        return new ContactDto(user, contactId, isAccepted, isBlacklisted);
    }

    public Contact mapToContact(ContactDto contactDto) {
        var contact = new Contact();
        var user = userService.getUserById(contactDto.user());
        if(user == null) {
            throw new EntityNotFoundException("User does not exist");
        }
        contact.setUser(user);
        var contactUser = userService.getUserById(contactDto.contact());
        if (contactUser == null) {
            throw new EntityNotFoundException("Contact does not exist");
        }
        contact.setContact(contactUser);
        contact.setIsAccepted(Boolean.TRUE.equals(contactDto.isAccepted()));
        contact.setIsBlacklisted(Boolean.TRUE.equals(contactDto.isBlacklisted()));

        return contact;
    }
}
