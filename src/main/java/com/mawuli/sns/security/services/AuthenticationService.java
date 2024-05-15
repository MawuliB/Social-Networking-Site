package com.mawuli.sns.security.services;

import com.mawuli.sns.domain.dto.mappers.UserMapper;
import com.mawuli.sns.domain.dto.request.UserDto;
import com.mawuli.sns.security.domain.dto.request.AuthenticationRequest;
import com.mawuli.sns.security.domain.dto.request.OAuthAuthenticationRequest;
import com.mawuli.sns.security.domain.dto.response.AuthenticationResponse;
import com.mawuli.sns.security.domain.dto.request.RegistrationRequest;
import com.mawuli.sns.security.domain.entities.RefreshToken;
import com.mawuli.sns.security.domain.entities.Status;
import com.mawuli.sns.security.domain.entities.Token;
import com.mawuli.sns.security.domain.entities.User;
import com.mawuli.sns.security.repositories.RefreshTokenRepository;
import com.mawuli.sns.security.repositories.RoleRepository;
import com.mawuli.sns.security.repositories.TokenRepository;
import com.mawuli.sns.security.repositories.UserRepository;
import com.mawuli.sns.utility.password.ValidatePassword;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    private final ValidatePassword validatePassword;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    private AuthenticationManager authenticationManager;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        //check if email or username exists
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Email already exists"
            );
        }

        //check if username exists
        if(userRepository.findByAlias(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Username already exists"
            );
        }

        //check for a valid password
        if(!validatePassword.isValidPassword(request.getPassword())) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Password must be at least 8 characters and contain at least one number, one uppercase letter, one lowercase letter and one special character"
            );
        }
        
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .alias(request.getUsername())
                .loginType("normal")
                .status(Status.OFFLINE)
                .newMessageCount(0)
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);

        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationCode(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                activationUrl,
                EmailTemplateName.ACTIVATION_ACCOUNT,
                newToken,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationCode(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder tokenBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            tokenBuilder.append(characters.charAt(randomIndex));
        }
        return tokenBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();
        claims.put("id", user.getId());
        claims.put("fullname", user.getFullName());
        claims.put("email", user.getEmail());

        var jwtToken = jwtService.generateToken(claims, user);


        // Check if a refresh token exists for the user
        RefreshToken refreshToken = getRefreshToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .user(UserMapper.mapToUserDto(user))
                .build();
    }

    private RefreshToken getRefreshToken(User user) {
        RefreshToken existingRefreshToken = refreshTokenRepository.findByUser(user);
        if (existingRefreshToken != null) {
            // Delete the existing refresh token
            refreshTokenRepository.delete(existingRefreshToken);
        }

        // Generate a new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(48))
                .token(UUID.randomUUID().toString())
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        // Find the refresh token
        RefreshToken refreshTokenFromDb = refreshTokenRepository.findByToken(refreshToken);

        // Check if it's expired
        if (refreshTokenFromDb.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Refresh token expired");
        }

        // Generate a new JWT
        User user = refreshTokenFromDb.getUser();
        var claims = new HashMap<String, Object>();
        claims.put("id", user.getId());
        claims.put("fullname", user.getFullName());
        claims.put("email", user.getEmail());

        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

//    @Transactional
    public UserDto activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        BAD_REQUEST,
                        "Invalid Token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Token expired. A new activation code has been sent to your email"
                    );
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        BAD_REQUEST,
                        "User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

        return UserMapper.mapToUserDto(user);

    }

    public AuthenticationResponse registerOAuthUser(String email, String name) {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        if(userRepository.findByEmail(email).isPresent()) {
            return authenticateOAuthUser(email);
        }

        String[] nameParts = name.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        var user = User.builder()
                .firstname(firstName)
                .lastname(lastName)
                .email(email)
                .alias(firstName)
                .loginType("oauth")
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);

        return authenticateOAuthUser(email);
    }

    private AuthenticationResponse authenticateOAuthUser(String email) {
        var request = new OAuthAuthenticationRequest();
        request.setEmail(email);
        return authenticateForOAuthUser(request);
    }

    public AuthenticationResponse authenticateForOAuthUser(OAuthAuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));
        var claims = new HashMap<String, Object>();
        claims.put("id", user.getId());
        claims.put("fullname", user.getFullName());
        claims.put("email", user.getEmail());

        // Check if a refresh token exists for the user
        RefreshToken refreshToken = getRefreshToken(user);

        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .user(UserMapper.mapToUserDto(user))
                .build();
    }
}