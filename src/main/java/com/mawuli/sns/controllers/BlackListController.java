package com.mawuli.sns.controllers;

import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.services.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class BlackListController {

    private final ContactService contactService;
    private final Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
//    private final String authenticatedUserEmail = authenticatedUser.getName();

    @MutationMapping
    void addToBlackList(@Argument("contactId") Integer contactId) {
        contactService.addContactToBlackList(contactId);
    }

    @MutationMapping
    void removeFromBlackList(@Argument("contactId") Integer blacklistId) {
        contactService.removeContactFromBlackList(blacklistId);
    }

}
