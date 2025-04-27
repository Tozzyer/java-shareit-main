package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceTests {

    private final UserMapper userMapper = new UserMapper();

    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    private final UserService userService = new UserService(userRepository, userMapper);

    @Test
    void createUserTest() {
        UserDto userDto = new UserDto(1L, "name", "test@gmail.com");
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("name");
        mockUser.setEmail("test@gmail.com");
        when(userRepository.getById(1L)).thenReturn(mockUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);
        Assertions.assertEquals(userDto, userMapper.toDto(userService.createUser(userDto)));
        Assertions.assertEquals(userDto, userMapper.toDto(userService.createUser(userDto)));
        Assertions.assertEquals(userDto, userMapper.toDto(userService.getUserById(1L)));
    }

    @Test
    void updateUser_SuccessfulUpdateTest() {
        long userId = 1L;
        UserDto updateDto = new UserDto(null, "Updated Name", "updated@example.com");
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_UserNotFoundTest() {
        long userId = 1L;
        UserDto updateDto = new UserDto(null, "Updated Name", "updated@example.com");
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");

        when(userMapper.fromDto(updateDto)).thenReturn(updatedUser);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                userService.updateUser(userId, updateDto)
        );

        assertEquals("User with id 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByIdTest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setId(userId);


        when(userRepository.getById(userId)).thenReturn(user);

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John", result.getName());

        verify(userRepository, times(1)).getById(userId);
    }

    @Test
    void deleteUserByIdTest() {
        long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

}
