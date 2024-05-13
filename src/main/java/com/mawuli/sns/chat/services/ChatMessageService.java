package com.mawuli.sns.chat.services;

import com.mawuli.sns.chat.domain.entity.ChatMessage;
import com.mawuli.sns.chat.repository.ChatMessageRepository;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.security.domain.entities.User;
import com.mawuli.sns.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        // Check if the recipient has blocked the sender or if the sender has been accepted by the recipient
        User recipientUser = userRepository.findById(Long.valueOf(chatMessage.getRecipientId()))
                .orElseThrow(() -> new ResponseStatusException(
                        BAD_REQUEST,
                        "User not found"));

        // Fetch the Contact record for the sender and recipient
        Contact contact = contactRepository.findByUserAndContact(
                userRepository.findById(Long.valueOf(chatMessage.getSenderId())).get(),
                userRepository.findById(Long.valueOf(chatMessage.getRecipientId())).get()
        ).orElseThrow(() -> new ResponseStatusException(
                BAD_REQUEST,
                "Contact not found"));
        Contact reverseContact = contactRepository.findByUserAndContact(
                userRepository.findById(Long.valueOf(chatMessage.getRecipientId())).get(),
                userRepository.findById(Long.valueOf(chatMessage.getSenderId())).get()
        ).orElseThrow(() -> new ResponseStatusException(
                BAD_REQUEST,
                "Contact not found"));

        // Check if the recipient has blocked the sender or if the sender has been accepted by the recipient and vice versa
        if (Boolean.TRUE.equals(contact.getIsBlacklisted()) || Boolean.FALSE.equals(contact.getIsAccepted()) ||
                Boolean.TRUE.equals(reverseContact.getIsBlacklisted()) || Boolean.FALSE.equals(reverseContact.getIsAccepted())){
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Message cannot be sent");
        }

        var chatId = chatRoomService.getChatRoomId(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                true
        ).orElseThrow();
        log.info("Generated chatId: {}", chatId);

        chatMessage.setChatId(chatId);

        // Increment newMessageCount for recipient user
        recipientUser.setNewMessageCount(recipientUser.getNewMessageCount() + 1);
        userRepository.save(recipientUser);

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);

        // Reset newMessageCount for recipient user
        User recipientUser = userRepository.findById(Long.valueOf(recipientId))
                .orElseThrow(() -> new ResponseStatusException(
                        BAD_REQUEST,
                        "User not found"));
        recipientUser.setNewMessageCount(0);
        userRepository.save(recipientUser);

        return chatId.map(chatMessageRepository::findByChatId).orElse(new ArrayList<>());
    }
}
