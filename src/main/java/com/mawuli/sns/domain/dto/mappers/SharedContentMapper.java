package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.SharedContentDto;
import com.mawuli.sns.domain.entities.SharedContent;
import com.mawuli.sns.security.domain.user.User;

public class SharedContentMapper {

    public static SharedContentDto mapToSharedContentDto(SharedContent sharedContent) {
        var user = sharedContent.getUser().getId();
        var receiver = sharedContent.getReceiver().getId();
        var content = sharedContent.getContent();
        var type = sharedContent.getType();
        var newContent = sharedContent.getNewContent();

        return new SharedContentDto(user, receiver, content, type, newContent);
    }

    public static SharedContent mapToSharedContent(SharedContentDto sharedContentDto) {
        var sharedContent = new SharedContent();
        var user = new User();
        user.setId(sharedContentDto.user());
        sharedContent.setUser(user);
        var receiver = new User();
        receiver.setId(sharedContentDto.receiver());
        sharedContent.setReceiver(receiver);
        sharedContent.setContent(sharedContentDto.content());
        sharedContent.setType(sharedContentDto.type());
        sharedContent.setNewContent(sharedContentDto.newContent());

        return sharedContent;
    }
}
