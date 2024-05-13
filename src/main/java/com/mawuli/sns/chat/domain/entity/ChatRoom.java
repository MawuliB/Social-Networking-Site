package com.mawuli.sns.chat.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String chatId;
    private String senderId;
    private String recipientId;
}
