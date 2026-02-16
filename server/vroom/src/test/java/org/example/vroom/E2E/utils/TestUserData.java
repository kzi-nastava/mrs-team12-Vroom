package org.example.vroom.E2E.utils;

public class TestUserData {
    private final String email;
    private final String password;

    public TestUserData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}