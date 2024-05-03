package com.mawuli.sns.services;

import com.mawuli.sns.exceptionhandler.graphql.EntityNotFoundException;
import com.mawuli.sns.repositories.UserAccessRepository;
import com.mawuli.sns.security.domain.user.User;
import com.mawuli.sns.utility.cloudinary.CloudinaryService;
import com.mawuli.sns.utility.fileUpload.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAccessRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    private final CloudinaryService cloudinaryService;

    @Value("${spring.profiles.active}")
    private String activeProfile;


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
            throw new EntityNotFoundException("User not found");
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

    public String setOrUpdateProfileImageUrl(MultipartFile profileImage, Integer id) throws IOException {
        User user = userRepository.findById(Long.valueOf(id)).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        if(activeProfile.equals("dev")) {
            String profileImageLocation = "profile-image";
            String fileLocation = uploadFile(profileImage, profileImageLocation, Math.toIntExact(user.getId()));
            user.setProfileImageUrl(fileLocation);
            userRepository.save(user);
            return fileLocation;
        }else {
            List<String> strings = uploadFileToCloudinary(profileImage);
            String fileLocation = strings.getFirst();
            String publicId = strings.get(1);
            user.setProfileImageUrl(fileLocation);
            user.setProfileImageId(publicId);
            userRepository.save(user);
            return fileLocation;
        }
    }

//    public void deleteProfileImage(Integer id) throws IOException {
//        User user = userRepository.findById(Long.valueOf(id)).orElse(null);
//        if (user == null) {
//            throw new EntityNotFoundException("User not found");
//        }
//
//        if(activeProfile.equals("dev")) {
//            fileStorageService.deleteFile(user.getProfileImageUrl());
//        }
//        else {
//            cloudinaryService.delete(user.getProfileImageUrl());
//        }
//    }

    private String uploadFile(MultipartFile file, String destination, Integer userId) {
        return fileStorageService.saveFile(file, destination, userId);
    }

    private List<String> uploadFileToCloudinary(MultipartFile file) throws IOException {
        BufferedImage bi = ImageIO.read(file.getInputStream());
        if (bi == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image not valid!");
        }
        Map upload = cloudinaryService.upload(file);
        return List.of(upload.get("url").toString(), upload.get("public_id").toString());
    }
}
