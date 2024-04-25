package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.BlackListDto;
import com.mawuli.sns.domain.entities.BlackList;
import com.mawuli.sns.security.domain.user.User;
import com.mawuli.sns.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackListMapper {

    private final UserService userService;

    public BlackListDto mapToBlackListDto(BlackList blackList) {
        if (blackList != null) {
            return new BlackListDto(blackList.getUser().getId(), blackList.getContact().getId());
        }
        return null;
    }

    public BlackList mapToBlackList(BlackListDto blackListDto) {
        var blackList = new BlackList();

        User user = userService.getUserById(blackListDto.user());
        blackList.setUser(user);

        User contact = userService.getUserById(blackListDto.contact());
        blackList.setContact(contact);

        return blackList;
    }
}
