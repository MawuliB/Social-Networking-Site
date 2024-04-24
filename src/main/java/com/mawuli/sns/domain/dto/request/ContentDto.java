package com.mawuli.sns.domain.dto.request;

public record ContentDto(
        Long user,
        String type,
        String content
) {
}
