package com.mawuli.sns.domain.dto.request;


public record ContactDto (
        Long user,
        Long contact,
        Boolean isAccepted
) {

}
