package com.mawuli.sns.security.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "First name is required")
    @NotBlank(message = "First name cannot be blank")
    private String firstname;

    @NotEmpty(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    private String lastname;

    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Username is required")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotEmpty(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;
}
