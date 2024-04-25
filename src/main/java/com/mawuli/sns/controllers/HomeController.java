package com.mawuli.sns.controllers;

import com.mawuli.sns.security.services.JwtService;
import com.mawuli.sns.services.TestService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("home")
@RequiredArgsConstructor
@Tag(name = "Home Controller", description = "Endpoints for the home page")
public class HomeController {

    private final JwtService jwtService;
    private final TestService testService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    String password1 = "dummyPassword1";
    String encodedPassword1 = passwordEncoder.encode(password1);

    String password2 = "dummyPassword2";
    String encodedPassword2 = passwordEncoder.encode(password2);

        @GetMapping("")
        public ResponseEntity<?> home(HttpServletRequest request) {
            String token = request.getHeader("Authorization").substring(7);
            Map<String, Object> claims = jwtService.decodeToken(token);
            String fullname = (String) claims.get("fullname");

            Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
            return ResponseEntity.ok("Hello you are authenticated as " + authenticatedUser.getName() + " with full name " + fullname + encodedPassword1 + " " + encodedPassword2);
        }

        @PostMapping(value = "/upload", consumes = "multipart/form-data")
        public ResponseEntity<?> upload(@Valid @Parameter @RequestPart("file") MultipartFile file) {
            var filename = testService.uploadFile(file, "description", 1);
            return ResponseEntity.ok("File uploaded successfully " + filename);
        }
}
