package com.mawuli.sns.chat.controllers;

import com.mawuli.sns.domain.dto.request.UserDto;
import com.mawuli.sns.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserChatController {

        private static final Logger log = LoggerFactory.getLogger(UserChatController.class);
        private final UserService userService;

        @MessageMapping("/user.loginUser")
        @SendTo("/user/public")
        public UserDto loginUser(
                @Payload Long id
        ) {
                return userService.loginUser(id);
        }

        @MessageMapping("/user.disconnectUser")
        @SendTo("/user/public")
        public String disconnectUser(
                @Payload Long userId
        ) {
            userService.disconnectUser(userId);
            return "User disconnected";
        }

        @GetMapping("/users")
        public ResponseEntity<List<UserDto>> findConnectedUsers(
                @Payload Long id
        ) {
            return ResponseEntity.ok(userService.findConnectedUsers(id));
        }

}
