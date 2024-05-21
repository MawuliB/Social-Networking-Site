package com.mawuli.sns.chat.controllers;

import com.mawuli.sns.chat.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("file-sharing")
public class FileSharingController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        Map response = chatMessageService.fileUpload(file);
        return ResponseEntity.ok(response);
    }
}
