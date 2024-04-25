package com.mawuli.sns.controllers;

import com.mawuli.sns.security.domain.user.User;
import com.mawuli.sns.services.UserService;
import lombok.RequiredArgsConstructor;
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

}
