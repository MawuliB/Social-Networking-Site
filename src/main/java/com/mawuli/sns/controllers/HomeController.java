package com.mawuli.sns.controllers;

import com.mawuli.sns.security.services.JwtService;
import com.mawuli.sns.services.TestService;
import com.mawuli.sns.services.UserService;
import com.mawuli.sns.utility.cloudinary.CloudinaryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Tag(name = "Home Controller", description = "Endpoints for the home page")
public class HomeController {

    private final JwtService jwtService;
    private final TestService testService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    String password1 = "dummyPassword1@";
    String encodedPassword1 = passwordEncoder.encode(password1);

    String password2 = "dummyPassword2@";
    String encodedPassword2 = passwordEncoder.encode(password2);

        @GetMapping("")
        public ResponseEntity<?> home(HttpServletRequest request) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String token = request.getHeader("Authorization").substring(7);
            Map<String, Object> claims = jwtService.decodeToken(token);
            String fullname = (String) claims.get("fullname");

            Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
            return ResponseEntity.ok("Hello you are authenticated as " + authenticatedUser.getName() + " with full name " + fullname + encodedPassword1 + " " + encodedPassword2);

        }

        @PostMapping(value = "/upload", consumes = "multipart/form-data")
        public ResponseEntity<?> upload(@Valid @Parameter @RequestPart("file") MultipartFile file) throws IOException {
            var filename = userService.setOrUpdateProfileImageUrl(file, 2);
            return ResponseEntity.ok("File uploaded successfully " + filename);
        }
}
