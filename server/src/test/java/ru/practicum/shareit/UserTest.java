package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void validUser_shouldPassManualChecks() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@example.com");

        assertNotNull(user.getName(), "Name should not be null");
        assertFalse(user.getName().isBlank(), "Name should not be blank");

        assertNotNull(user.getEmail(), "Email should not be null");
        assertFalse(user.getEmail().isBlank(), "Email should not be blank");
    }

    @Test
    void blankEmail_shouldFailManualCheck() {
        User user = new User();
        user.setName("Valid Name");
        user.setEmail("  ");

        assertNotNull(user.getEmail(), "Email should not be null");
        assertTrue(user.getEmail().isBlank(), "Email should be considered blank");
    }
}
