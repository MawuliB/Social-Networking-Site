package com.mawuli.sns.user;

import com.mawuli.sns.domain.dto.mappers.UserMapper;
import com.mawuli.sns.domain.dto.request.UserDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.repositories.UserAccessRepository;
import com.mawuli.sns.security.domain.entities.User;
import com.mawuli.sns.security.repositories.UserRepository;
import com.mawuli.sns.security.services.JwtService;
import com.mawuli.sns.services.UserService;
import com.mawuli.sns.utility.cloudinary.CloudinaryService;
import com.mawuli.sns.utility.fileUpload.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserAccessRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private ContactRepository contactRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserById() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUpdateUser() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("newUsername");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(user.getUserName())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.updateUser(user, userId);

        assertEquals(UserMapper.mapToUserDto(user), result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByUsername(user.getUserName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testPartialUpdateUser() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("newUsername");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(user.getUserName())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.partialUpdateUser(user, userId);

        assertEquals(UserMapper.mapToUserDto(user), result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByUsername(user.getUserName());
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testUpdatePassword() {
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        User user = new User();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(oldPassword));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        userService.updatePassword(newPassword, oldPassword, userId);

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(oldPassword, user.getPassword());
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetUserByToken() {
        String token = "token";
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId.intValue());

        when(jwtService.decodeToken(token)).thenReturn(claims);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserByToken(token);

        assertEquals(UserMapper.mapToUserDto(user), result);
        verify(jwtService, times(1)).decodeToken(token);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testLoginUser() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.loginUser(userId);

        assertEquals(UserMapper.mapToUserDto(user), result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDisconnectUser() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.disconnectUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testFindConnectedUsers() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Contact contact1 = new Contact();
        contact1.setUser(user);
        contact1.setContact(new User());

        Contact contact2 = new Contact();
        contact2.setUser(user);
        contact2.setContact(new User());

        List<Contact> contacts = Arrays.asList(contact1, contact2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByUser(user)).thenReturn(contacts);

        List<UserDto> result = userService.findConnectedUsers(userId);

        assertEquals(contacts.size(), result.size());
        verify(userRepository, times(1)).findById(userId);
        verify(contactRepository, times(1)).findAllByUser(user);
    }
}