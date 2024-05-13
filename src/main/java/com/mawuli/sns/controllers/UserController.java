package com.mawuli.sns.controllers;

import com.mawuli.sns.domain.dto.request.UserDto;
import com.mawuli.sns.security.domain.entities.User;
import com.mawuli.sns.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;
        private final Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
//    private final String authenticatedUserEmail = authenticatedUser.getName();

        @QueryMapping
        List<User> getAllUsers() {
            return userService.getAllUsers();
        }

        @QueryMapping
        public UserDto getUserByToken(@Argument("token") String token) {
                return userService.getUserByToken(token);
        }

        @MutationMapping
        public UserDto updateUser(@Argument("user") User user, @Argument("id") Long id) {
                return userService.partialUpdateUser(user, id);
        }

        @MutationMapping
        public void updatePassword(@Argument("newPassword") String newPassword, @Argument("oldPassword") String oldPassword, @Argument("id") Long id) {
                userService.updatePassword(newPassword, oldPassword, id);
        }


//        @MutationMapping
//        public String setOrUpdateProfileImageUrl(@Argument("profileImage") MultipartFile profileImage, @Argument("id") Integer id) throws IOException {
//                return userService.setOrUpdateProfileImageUrl(profileImage, id);
//        }
}
