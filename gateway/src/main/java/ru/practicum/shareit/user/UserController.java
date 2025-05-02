package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BadRequestException("Email failed");
        }
        return userClient.createUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") long id, @Valid @RequestBody UserDto user) {
        return userClient.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable("id") long id) {
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") long id) {
        userClient.deleteUserById(id);
    }

}
