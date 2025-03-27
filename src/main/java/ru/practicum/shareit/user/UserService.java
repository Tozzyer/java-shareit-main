package ru.practicum.shareit.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            if (user.getName() != null && !user.getName().isBlank()) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                existingUser.setEmail(user.getEmail());
            }
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public User getUserById(long id) {
        return userRepository.getById(id);
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

}
