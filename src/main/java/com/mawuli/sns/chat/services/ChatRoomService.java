package com.mawuli.sns.chat.services;

import com.mawuli.sns.chat.domain.entity.ChatRoom;
import com.mawuli.sns.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(
            String senderId,
            String recipientId,
            boolean createIfNotExist
    ) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createIfNotExist) {
                       var chatId = createChatId(senderId, recipientId);

                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);

        var chatRoomSenderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        var chatRoomRecipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        chatRoomRepository.save(chatRoomSenderRecipient);
        chatRoomRepository.save(chatRoomRecipientSender);
        return chatId;
    }
}
