package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.UserDto;
import com.mawuli.sns.security.domain.user.User;

public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        var firstname = user.getFirstname();
        var lastname = user.getLastname();
        var username = user.getUsername();
        var email = user.getEmail();
        var profilePictureUrl = user.getProfileImageUrl();

        return new UserDto(firstname, lastname, username, email, profilePictureUrl);
    }

    public static User mapToUser(UserDto userDto) {
        var user = new User();
        user.setFirstname(userDto.firstname());
        user.setLastname(userDto.lastname());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setProfileImageUrl(userDto.profilePictureUrl());

        return user;
    }
}
