package com.mawuli.sns.chat.repository;

import com.mawuli.sns.chat.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String string);
}
