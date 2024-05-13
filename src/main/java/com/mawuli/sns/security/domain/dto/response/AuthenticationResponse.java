package com.mawuli.sns.security.domain.dto.response;

import com.mawuli.sns.domain.dto.request.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {

    private String token;
    private String refreshToken;
    private UserDto user;
}
