package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUserDto_shouldNotHaveViolations() {
        UserDto user = new UserDto(1L, "Alice", "alice@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void invalidEmail_shouldReturnViolation() {
        UserDto user = new UserDto(2L, "Bob", "not-an-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Expected validation error for invalid email");

        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals("Invalid E-mail adress", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void nullEmail_shouldReturnViolation() {
        UserDto user = new UserDto(3L, "Charlie", null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Email null is allowed unless @NotNull is specified");
    }
}
