package com.mawuli.sns.services;

import com.mawuli.sns.repositories.UserAccessRepository;
import com.mawuli.sns.security.domain.user.User;
import com.mawuli.sns.utility.fileUpload.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAccessRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;


    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(User user, Long id) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }
        user.setId(id);
        return userRepository.save(user);
    }

    public User partialUpdateUser(User user, Long id) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if(user.getProfileImageUrl() != null) {
            existingUser.setProfileImageUrl(user.getProfileImageUrl());
        }
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).orElse(null);
    }

    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).toList();
    }

    public void updatePassword(String newPassword, String oldPassword, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) { // check if old password is correct
            throw new ResponseStatusException(BAD_REQUEST, "Old password is incorrect");
        }

        if(newPassword.equals(oldPassword)) { // check if new password is the same as old password
            throw new ResponseStatusException(BAD_REQUEST, "New password cannot be the same as old password");
        }

        String password = passwordEncoder.encode(newPassword);
        user.setPassword(password);
        userRepository.save(user);
    }

    public void setOrUpdateProfileImageUrl(MultipartFile profileImageUrl, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }
        String fileLocation = uploadFile(profileImageUrl, "profile-image", Math.toIntExact(user.getId()));
        user.setProfileImageUrl(fileLocation);
        userRepository.save(user);
    }

    private String uploadFile(MultipartFile file, String description, Integer userId) {
        return fileStorageService.saveFile(file, description, userId);
    }
}
