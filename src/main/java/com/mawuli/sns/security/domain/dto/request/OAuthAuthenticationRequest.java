package com.mawuli.sns.security.domain.dto.request;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthAuthenticationRequest {
    private String email;
}
