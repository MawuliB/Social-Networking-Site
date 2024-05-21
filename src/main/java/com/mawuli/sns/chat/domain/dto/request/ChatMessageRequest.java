package com.mawuli.sns.chat.domain.dto.request;

import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
    private Object content;
    private String senderId;
    private String recipientId;
    private String fileType;
    private Date timestamp;
}
