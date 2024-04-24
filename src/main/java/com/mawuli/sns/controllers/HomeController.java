package com.mawuli.sns.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("home")
@RequiredArgsConstructor
@Tag(name = "Home Controller", description = "Endpoints for the home page")
public class HomeController {

        @GetMapping("")
        public ResponseEntity<?> home(Authentication authenticatedUser) {
            return ResponseEntity.ok("Hello you are authenticated as " + authenticatedUser.getName());
        }
}
