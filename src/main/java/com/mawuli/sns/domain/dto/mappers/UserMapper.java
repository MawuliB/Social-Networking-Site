package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.UserDto;
import com.mawuli.sns.security.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        var id = user.getId();
        var firstname = user.getFirstname();
        var lastname = user.getLastname();
        var username = user.getUserName();
        var email = user.getEmail();
        var profilePictureUrl = user.getProfileImageUrl();
        var profilePictureId = user.getProfileImageId();

        return new UserDto(id, firstname, lastname, username, email, profilePictureUrl, profilePictureId);
    }

    public static User mapToUser(UserDto userDto) {
        var user = new User();
        user.setFirstname(userDto.firstname());
        user.setLastname(userDto.lastname());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setProfileImageUrl(userDto.profileImageUrl());
        user.setProfileImageId(userDto.profileImageId());

        return user;
    }
}
