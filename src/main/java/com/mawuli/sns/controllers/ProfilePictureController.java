package com.mawuli.sns.controllers;

import com.mawuli.sns.services.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController()
@RequiredArgsConstructor
@RequestMapping("profile")
@Tag(name = "Profile Picture Controller", description = "Endpoints for profile picture")
public class ProfilePictureController {
    private final UserService userService;

    @PostMapping(value = "/upload/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> upload(@Valid @Parameter @RequestPart("file") MultipartFile file, @PathVariable Integer id) throws IOException {

        var filename = userService.setOrUpdateProfileImageUrl(file, id);
        return ResponseEntity.ok(filename);
    }
}
