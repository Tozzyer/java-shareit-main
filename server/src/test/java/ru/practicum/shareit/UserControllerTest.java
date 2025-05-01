package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDto userDto = new UserDto(null, "Alice", "alice@example.com");
        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setName("Alice");
        createdUser.setEmail("alice@example.com");

        Mockito.when(userService.createUser(userDto)).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.email", is("alice@example.com")));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        long userId = 1L;
        UserDto updateDto = new UserDto(null, "Updated", "updated@example.com");
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated");
        updatedUser.setEmail("updated@example.com");

        Mockito.when(userService.updateUser(userId, updateDto)).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.name", is("Updated")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));
    }

    @Test
    void findUserById_shouldReturnUser() throws Exception {
        long userId = 2L;
        User user = new User();
        user.setId(userId);
        user.setName("Bob");
        user.setEmail("bob@example.com");

        Mockito.when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.name", is("Bob")))
                .andExpect(jsonPath("$.email", is("bob@example.com")));
    }

    @Test
    void deleteUserById_shouldReturnNoContent() throws Exception {
        long userId = 3L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUserById(userId);
    }
}
