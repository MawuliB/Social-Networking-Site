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
                + "(?=.*[^a-zA-Z0-9 ])" // This will check for any character that is not a letter, number or space
                + "(?=\\S+$).{8,}$";
        return password.matches(regex);
    }
}
