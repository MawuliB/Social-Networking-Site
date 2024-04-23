package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.BlackListDto;
import com.mawuli.sns.domain.entities.BlackList;
import com.mawuli.sns.security.domain.user.User;

public class BlackListMapper {

    public static BlackListDto mapToBlackListDto(BlackList blackList) {
        if (blackList != null) {
            return new BlackListDto(blackList.getUser().getId(), blackList.getContact().getId());
        }
        return null;
    }

    public static BlackList mapToBlackList(BlackListDto blackListDto) {
        var blackList = new BlackList();
        var user = new User();
        user.setId(blackListDto.user());
        blackList.setUser(user);
        var contact = new User();
        contact.setId(blackListDto.contact());
        blackList.setContact(contact);

        return blackList;
    }
}
