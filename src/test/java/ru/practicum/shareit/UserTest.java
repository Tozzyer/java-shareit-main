package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUser_shouldNotHaveViolations() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User with valid data should not cause violations");
    }

    @Test
    void blankName_shouldCauseViolation() {
        User user = new User();
        user.setName("  ");
        user.setEmail("user@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Blank name should cause a violation");

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Имя не должно быть пустым", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void invalidEmail_shouldCauseViolation() {
        User user = new User();
        user.setName("Valid Name");
        user.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Invalid email should cause a violation");

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Адрес электронной почты должен быть корректен", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void blankEmail_shouldCauseViolation() {
        User user = new User();
        user.setName("Valid Name");
        user.setEmail("  ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Blank email should cause a violation");


    }
}
