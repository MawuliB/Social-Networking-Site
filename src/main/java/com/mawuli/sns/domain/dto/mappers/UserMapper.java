package com.mawuli.sns.domain.dto.mappers;

import com.mawuli.sns.domain.dto.request.UserDto;
import com.mawuli.sns.security.domain.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        var id = user.getId();
        var firstname = user.getFirstname();
        var lastname = user.getLastname();
        var alias = user.getAlias();
        var email = user.getEmail();
        var profilePictureUrl = user.getProfileImageUrl();
        var profilePictureId = user.getProfileImageId();
        var status = user.getStatus();
        var newMessageCount = user.getNewMessageCount();

        return new UserDto(id, firstname, lastname, alias, email, profilePictureUrl, profilePictureId, status, newMessageCount);
    }

    public static User mapToUser(UserDto userDto) {
        var user = new User();
        user.setFirstname(userDto.firstname());
        user.setLastname(userDto.lastname());
        user.setAlias(userDto.alias());
        user.setEmail(userDto.email());
        user.setProfileImageUrl(userDto.profileImageUrl());
        user.setProfileImageId(userDto.profileImageId());
        user.setStatus(userDto.status());
        user.setNewMessageCount(userDto.newMessageCount());

        return user;
    }

    public static List<UserDto> mapToUserDtoList(List<User> allByStatus) {
        return allByStatus.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
