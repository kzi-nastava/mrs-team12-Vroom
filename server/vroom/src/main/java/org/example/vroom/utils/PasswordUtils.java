package org.example.vroom.utils;

import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    public boolean isPasswordValid(String pass){
        if(pass == null || pass.isEmpty() ||
                pass.length() < 8 || !pass.matches(".*[0-9].*") ||
                !pass.matches(".*[a-z].*") || !pass.matches(".*[A-Z].*"))
            return false;

        return true;
    }

}
