package com.mawuli.sns.controllers;

import com.mawuli.sns.domain.dto.mappers.BlackListMapper;
import com.mawuli.sns.domain.dto.request.BlackListDto;
import com.mawuli.sns.domain.entities.BlackList;
import com.mawuli.sns.services.BlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@RequiredArgsConstructor
@Slf4j
public class BlackListController {

    private final BlackListService blackListService;
    private final BlackListMapper blackListMapper;
    private final Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
//    private final String authenticatedUserEmail = authenticatedUser.getName();

    @MutationMapping
    BlackList addToBlackList(@Argument("blacklist") BlackListDto blacklist) {
        return blackListService.addContactToBlackList(blacklist);
    }

    @MutationMapping
    void removeFromBlackList(@Argument("blacklist") Integer blacklistId) {
        blackListService.removeContactFromBlackList(blacklistId);
    }

    @QueryMapping
    List<BlackList> getBlackListByContactId(@Argument("contactId") Integer contactId) {
        return blackListService.getBlackListByContactId(Long.valueOf(contactId));
    }

}
