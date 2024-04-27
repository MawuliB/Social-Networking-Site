package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.security.domain.user.User;
import com.mawuli.sns.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactMapper {

    private final UserService userService;

    public static ContactDto mapToContactDto(Contact contact) {
        var user = contact.getUser().getId();
        var contactId = contact.getContact().getId();
        var isAccepted = contact.getIsAccepted();

        return new ContactDto(user, contactId, isAccepted);
    }

    public static Contact mapToContact(ContactDto contactDto) {
        var contact = new Contact();
        var user = new User();
        user.setId(contactDto.user());
        contact.setUser(user);
        var contactUser = new User();
        contactUser.setId(contactDto.contact());
        contact.setContact(contactUser);
        contact.setIsAccepted(contactDto.isAccepted());

        return contact;
    }
}
