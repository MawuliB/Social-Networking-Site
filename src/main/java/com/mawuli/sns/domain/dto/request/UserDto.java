package com.mawuli.sns.domain.dto.request;

import com.mawuli.sns.security.domain.entities.Status;
import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String firstname,
        String lastname,
        String username,
        String email,
        String profileImageUrl,
        String profileImageId,
        Status status,
        Integer newMessageCount
) {
}
