package com.sns.oauth2.controllers;


import com.sns.oauth2.dtos.MessageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    @GetMapping("/public/messages")
    public ResponseEntity<MessageDto> publicMessages() {
        return ResponseEntity.ok(new MessageDto("public content"));
    }
}