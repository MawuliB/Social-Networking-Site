package com.mawuli.sns.utility.password;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidatePassword {

    public boolean isValidPassword(String password) {
        // Regex to check valid password for a combination of upper, lower, special characters and numbers.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,100}$";
        return password.matches(regex);
    }
}
