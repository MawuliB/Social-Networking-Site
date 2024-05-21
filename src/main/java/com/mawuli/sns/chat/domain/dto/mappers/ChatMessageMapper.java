package com.mawuli.sns.chat.domain.dto.mappers;

import com.mawuli.sns.chat.domain.dto.request.ChatMessageRequest;
import com.mawuli.sns.chat.domain.entity.ChatMessage;
import com.mawuli.sns.chat.domain.entity.FileType;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageMapper {

    public static ChatMessage mapToChatMessage(ChatMessageRequest chatMessageRequest) {
        return ChatMessage.builder()
                .content((String) chatMessageRequest.getContent())
                .senderId(chatMessageRequest.getSenderId())
                .recipientId(chatMessageRequest.getRecipientId())
                .fileType(FileType.valueOf(chatMessageRequest.getFileType()))
                .timestamp(chatMessageRequest.getTimestamp())
                .build();
    }
}
