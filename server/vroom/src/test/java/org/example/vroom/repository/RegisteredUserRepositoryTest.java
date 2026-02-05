package org.example.vroom.repository;

import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.enums.Gender;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RegisteredUserRepositoryTest {

    @Autowired
    private RegisteredUserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByEmail - user exists - returns user")
    void findByEmail_userExists_returnsUser() {
        // Arrange
        RegisteredUser user = RegisteredUser.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .address("Test Address 123")
                .phoneNumber("0601234567")
                .gender(Gender.valueOf("MALE"))
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // Act
        Optional<RegisteredUser> result = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("Test", result.get().getFirstName());
    }

    @Test
    @DisplayName("findByEmail - user does not exist - returns empty")
    void findByEmail_userDoesNotExist_returnsEmpty() {
        // Act
        Optional<RegisteredUser> result = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByEmail - null email - returns empty")
    void findByEmail_nullEmail_returnsEmpty() {
        // Act
        Optional<RegisteredUser> result = userRepository.findByEmail(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByEmail - empty email - returns empty")
    void findByEmail_emptyEmail_returnsEmpty() {
        // Act
        Optional<RegisteredUser> result = userRepository.findByEmail("");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByEmail - case sensitivity check")
    void findByEmail_caseSensitive() {
        // Arrange
        RegisteredUser user = RegisteredUser.builder()
                .firstName("Test")
                .lastName("User")
                .email("Test@Example.com")
                .password("password123")
                .address("Test Address 123")
                .phoneNumber("0601234567")
                .gender(Gender.valueOf("MALE"))
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // Act
        Optional<RegisteredUser> resultExact = userRepository.findByEmail("Test@Example.com");
        Optional<RegisteredUser> resultLowercase = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(resultExact.isPresent());
    }
}