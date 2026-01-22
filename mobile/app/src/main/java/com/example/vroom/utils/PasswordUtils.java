package com.example.vroom.utils;

import android.widget.EditText;

public class PasswordUtils {

    public static void isPasswordValid(String pass, String rePass) throws Exception{
        if (pass.length() < 8) {
            throw new Exception("Password must be at least 8 characters long");
        }

        if (!pass.matches(".*[0-9].*")) {
            throw new Exception("Password must contain a number");
        }

        if (!pass.matches(".*[a-z].*")) {
            throw new Exception("Password must contain a lowercase letter");
        }

        if (!pass.matches(".*[A-Z].*")) {
            throw new Exception("Password must contain an uppercase letter");
        }

        if(!pass.equals(rePass))
            throw new Exception("Password must match");
    }
}
