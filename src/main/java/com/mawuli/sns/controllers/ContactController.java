package com.mawuli.sns.controllers;

import com.mawuli.sns.domain.dto.mappers.BlackListMapper;
import com.mawuli.sns.domain.dto.request.BlackListDto;
import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.BlackList;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.services.BlackListService;
import com.mawuli.sns.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @MutationMapping
    Contact addToContact(@Argument("contact") ContactDto contact) {
        return contactService.addContactToContact(contact);
    }

    @MutationMapping
    void removeFromContact(@Argument("contactId") Integer contactId) {
        contactService.removeContactFromContact(contactId);
    }

    @QueryMapping
    List<Contact> getContactByContactId(@Argument("contactId") Integer contactId) {
        return contactService.getContactByContactId(Long.valueOf(contactId));
    }
}
