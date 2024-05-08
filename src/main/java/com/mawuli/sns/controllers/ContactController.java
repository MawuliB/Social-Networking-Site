package com.mawuli.sns.controllers;

import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @MutationMapping
    Contact addToContact(@Argument("contact") ContactDto contact) {
        return contactService.addContactToContactList(contact);
    }

    @MutationMapping
    void removeFromContact(@Argument("contactId") Integer contactId) {
        contactService.removeContactFromContactList(contactId);
    }

    @MutationMapping
    void acceptContactInvitation(@Argument("contactId") Integer contactId) {
        contactService.acceptContactInvitation(contactId);
    }

    @QueryMapping
    List<Contact> getAllContactByContactId(@Argument("contactId") Integer contactId) {
        return contactService.getAllContactByContactId(Long.valueOf(contactId));
    }

    @QueryMapping
    List<Contact> getInvitationsByUserId(@Argument("userId") Integer userId) {
        return contactService.getInvitationsByUserId(Long.valueOf(userId));
    }
}
