package com.mawuli.sns.chat.controllers;

import com.mawuli.sns.chat.domain.dto.request.ChatMessageRequest;
import com.mawuli.sns.chat.domain.entity.ChatMessage;
import com.mawuli.sns.chat.domain.entity.ChatNotification;
import com.mawuli.sns.chat.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessageRequest chatMessage) {
        try{
        ChatMessage savedMessage = chatMessageService.save(chatMessage);
        if("You cannot send messages to this user, You have been blocked or not accepted as a friend".equals(savedMessage.getContent()))
        {
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getSenderId(),
                    "/queue/messages",
                    new ChatNotification(
                            savedMessage.getId(),
                            savedMessage.getSenderId(),
                            savedMessage.getRecipientId(),
                            savedMessage.getContent(),
                            savedMessage.getFileType(),
                            "error"
                    )
            );
        }else{
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),
                "/queue/messages",
                new ChatNotification(
                        savedMessage.getId(),
                        savedMessage.getSenderId(),
                        savedMessage.getRecipientId(),
                        savedMessage.getContent(),
                        savedMessage.getFileType(),
                        "message"
                )
        );
        }
        } catch (ResponseStatusException e) {
            // Send the error message to the "/queue/errors" destination
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getSenderId(),
                    "/queue/errors",
                    e.getMessage()
            );
        }
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId
    ) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @MessageExceptionHandler
    @SendTo("/queue/errors")
    public ResponseEntity<?> handleException(MessageDeliveryException exception) {
        // Return a message to the user
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
