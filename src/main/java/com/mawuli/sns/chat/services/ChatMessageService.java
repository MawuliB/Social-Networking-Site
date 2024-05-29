package com.mawuli.sns.chat.services;

import com.mawuli.sns.chat.domain.dto.mappers.ChatMessageMapper;
import com.mawuli.sns.chat.domain.dto.request.ChatMessageRequest;
import com.mawuli.sns.chat.domain.entity.ChatMessage;
import com.mawuli.sns.chat.repository.ChatMessageRepository;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.security.domain.entities.User;
import com.mawuli.sns.security.repositories.UserRepository;
import com.mawuli.sns.utility.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final CloudinaryService cloudinaryService;

    public ChatMessage save(ChatMessageRequest chatMessage) {

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
            return ChatMessageMapper.mapToChatMessage(ChatMessageRequest.builder()
                    .senderId(chatMessage.getSenderId())
                    .recipientId(chatMessage.getRecipientId())
                    .content("You cannot send messages to this user, You have been blocked or not accepted as a friend")
                    .timestamp(new Date())
                    .fileType("TEXT")
                    .build());
        }

        // Get the chatId
        var chatId = chatRoomService.getChatRoomId(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                true
        ).orElseThrow();

        // Check if the message is a text, image or video
        if(Objects.equals(chatMessage.getFileType(), "TEXT")) {
            chatMessage.setFileType("TEXT");
        }else if(Objects.equals(chatMessage.getFileType(), "IMAGE")) {
            chatMessage.setFileType("IMAGE");
        }else if(Objects.equals(chatMessage.getFileType(), "VIDEO")) {
            chatMessage.setFileType("VIDEO");
        } else {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Invalid file type");
        }

        ChatMessage chatMessageToSaved = ChatMessageMapper.mapToChatMessage(chatMessage);
        chatMessageToSaved.setChatId(chatId);

        // Increment newMessageCount for recipient user
        recipientUser.setNewMessageCount(recipientUser.getNewMessageCount() + 1);
        userRepository.save(recipientUser);

        return chatMessageRepository.save(chatMessageToSaved);
    }

    public Map fileUpload(MultipartFile file) throws IOException {
        if(isImage(file)) {
            Map result = new HashMap<>();
            result.put("url", uploadImageToCloudinary(file));
            result.put("fileType", "IMAGE");
            return result;
        } else if(isVideo(file)) {
            Map result = new HashMap<>();
            result.put("url", uploadVideoToCloudinary(file));
            result.put("fileType", "VIDEO");
            return result;
        } else {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Invalid file type");
        }
    }

    private Boolean isImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }

    private Boolean isVideo(MultipartFile file) {
        return Objects.equals(file.getContentType(), "video/mp4") || Objects.equals(file.getContentType(), "video/quicktime") || Objects.equals(file.getContentType(), "video/mov") || Objects.equals(file.getContentType(), "video/avi") || Objects.equals(file.getContentType(), "video/wmv") || Objects.equals(file.getContentType(), "video/flv") || Objects.equals(file.getContentType(), "video/mkv") || Objects.equals(file.getContentType(), "video/3gp") || Objects.equals(file.getContentType(), "video/mpeg") || Objects.equals(file.getContentType(), "video/webm") || Objects.equals(file.getContentType(), "video/ogg") || Objects.equals(file.getContentType(), "video/ogv") || Objects.equals(file.getContentType(), "video/ogx") || Objects.equals(file.getContentType(), "video/3g2") || Objects.equals(file.getContentType(), "video/3gpp") || Objects.equals(file.getContentType(), "video/3gpp2");
    }

    private String uploadImageToCloudinary(MultipartFile file) throws IOException {
        Map upload = cloudinaryService.upload(file);
        return upload.get("url").toString();
    }

    private String uploadVideoToCloudinary(MultipartFile file) throws IOException {
        Map upload = cloudinaryService.videoUpload(file);
        return upload.get("url").toString();
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

        return chatId.map(chatMessageRepository::findByChatIdOrderByTimestamp).orElse(new ArrayList<>());
    }
}
