package com.mawuli.sns.utility.mappers;

import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.security.domain.user.User;

public class ContactMapper {

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
