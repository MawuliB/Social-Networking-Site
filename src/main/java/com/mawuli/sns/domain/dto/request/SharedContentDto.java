package com.mawuli.sns.domain.dto.request;

public record SharedContentDto(
        Long user,
        Long receiver,
        String content,
        String type,
        Boolean newContent
) {

}
