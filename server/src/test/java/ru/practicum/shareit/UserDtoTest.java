package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void validUserDto_shouldPassManualChecks() {
        UserDto user = new UserDto(1L, "Alice", "alice@example.com");

        assertNotNull(user.getName(), "Name should not be null");
        assertFalse(user.getName().isBlank(), "Name should not be blank");

        assertNotNull(user.getEmail(), "Email should not be null");
        assertFalse(user.getEmail().isBlank(), "Email should not be blank");
        assertTrue(user.getEmail().contains("@"), "Email should contain '@'");
    }

    @Test
    void invalidEmail_shouldFailManualCheck() {
        UserDto user = new UserDto(2L, "Bob", "not-an-email");

        assertFalse(user.getEmail().contains("@"), "Email should be considered invalid");
    }

    @Test
    void nullEmail_shouldPassIfAllowed() {
        UserDto user = new UserDto(3L, "Charlie", null);

        assertNull(user.getEmail(), "Email may be null unless restricted");
    }
}
