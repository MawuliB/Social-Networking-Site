package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.ContentDto;
import com.mawuli.sns.domain.entities.Content;
import com.mawuli.sns.security.domain.entities.User;
import org.springframework.stereotype.Service;

@Service
public class ContentMapper {

    public static ContentDto mapToContentDto(Content content) {
        var user = content.getUser().getId();
        var contentObj = content.getContent();
        var type = content.getType();

        return new ContentDto(user, type, contentObj);
    }

    public static Content mapToContent(ContentDto contentDto) {
        var content = new Content();
        var user = new User();
        user.setId(contentDto.user());
        content.setUser(user);
        content.setContent(contentDto.content());
        content.setType(contentDto.type());

        return content;
    }
}
