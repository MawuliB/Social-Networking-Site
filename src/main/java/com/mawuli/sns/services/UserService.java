package com.mawuli.sns.services;

import com.mawuli.sns.domain.dto.mappers.UserMapper;
import com.mawuli.sns.domain.dto.request.UserDto;
import com.mawuli.sns.exceptionhandler.graphql.EntityNotFoundException;
import com.mawuli.sns.exceptionhandler.graphql.InvalidOldPasswordException;
import com.mawuli.sns.exceptionhandler.graphql.UsernameAlreadyExistException;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.repositories.UserAccessRepository;
import com.mawuli.sns.security.domain.entities.Status;
import com.mawuli.sns.security.domain.entities.User;
import com.mawuli.sns.security.services.JwtService;
import com.mawuli.sns.utility.cloudinary.CloudinaryService;
import com.mawuli.sns.utility.fileUpload.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private final UserAccessRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final ContactRepository contactRepository;

    private final JwtService jwtService;

    private final CloudinaryService cloudinaryService;

    @Value("${spring.profiles.active}")
    private String activeProfile;


    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserDto updateUser(User user, Long id) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }

        if(!user.getUserName().equals(existingUser.getUserName())) {
            if(userRepository.findByUsername(user.getUserName()).isPresent()) {
                throw new UsernameAlreadyExistException(
                        BAD_REQUEST,
                        "Username already exists"
                );
            }
        }

        user.setId(id);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    public UserDto partialUpdateUser(User user, Long id) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        if (user.getUserName() != null) {
            if(!user.getUserName().equals(existingUser.getUserName())) {
                Optional<User> userWithNewUsername = userRepository.findByUsername(user.getUserName());
                if(userWithNewUsername.isPresent()) {
                    throw new UsernameAlreadyExistException(
                            HttpStatus.BAD_REQUEST, "Username already exists");
                }
            }
            existingUser.setUsername(user.getUserName());
        }
        if (user.getFirstname() != null) {
            existingUser.setFirstname(user.getFirstname());
        }
        if (user.getLastname() != null) {
            existingUser.setLastname(user.getLastname());
        }
        return UserMapper.mapToUserDto(userRepository.save(existingUser));
    }

    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).toList();
    }

    public void updatePassword(String newPassword, String oldPassword, Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        log.error("Old password: {}", oldPassword);

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) { // check if old password is correct
            throw new InvalidOldPasswordException(BAD_REQUEST, "Old password is incorrect");
        }

        if(newPassword.equals(oldPassword)) { // check if new password is the same as old password
            throw new UsernameAlreadyExistException(BAD_REQUEST, "New password cannot be the same as old password");
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

    public UserDto getUserByToken(String token) {
        Map<String, Object> claims = jwtService.decodeToken(token);
        Long id = Long.valueOf((Integer) claims.get("id"));
        return UserMapper.mapToUserDto(Objects.requireNonNull(userRepository.findById(id).orElse(null)));
    }

    public UserDto loginUser(Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setStatus(Status.ONLINE);
            userRepository.save(user);
        }
        return UserMapper.mapToUserDto(user);
    }

    public void disconnectUser(Long id) {
        var savedUser = userRepository.findById(id).orElse(null);
        if (savedUser != null) {
            savedUser.setStatus(Status.OFFLINE);
            userRepository.save(savedUser);
        }
    }

    public List<UserDto> findConnectedUsers() {
        return UserMapper.mapToUserDtoList(userRepository.findAllByStatus(Status.ONLINE));
    }

    //return contact column from a list of contacts
    public List<UserDto> findConnectedUsers(Long id) {
        var contacts = contactRepository.findAllByUser(userRepository.findById(id).orElse(null));
        List<UserDto> connectedUsers = new ArrayList<>();
        for (var contact : contacts) {
                connectedUsers.add(UserMapper.mapToUserDto(contact.getContact()));
        }
        return connectedUsers;
    }
}
