package com.mawuli.sns.domain.dto.request;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String firstname,
        String lastname,
        String username,
        String email,
        String profilePictureUrl,
        String profilePictureId
) {
}
