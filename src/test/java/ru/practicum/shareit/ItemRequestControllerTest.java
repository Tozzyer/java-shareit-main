package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        long userId = 1L;
        ItemRequestDto inputDto = new ItemRequestDto(0L, "Need a drill", null, List.of());
        ItemRequestDto returnedDto = new ItemRequestDto(100L, "Need a drill", LocalDateTime.now(), List.of());

        Mockito.when(requestService.createRequest(Mockito.any(), Mockito.eq(userId)))
                .thenReturn(returnedDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.description", is("Need a drill")));
    }


    @Test
    void getRequestsByUserId_shouldReturnListOfRequests() throws Exception {
        long userId = 2L;
        LocalDateTime now = LocalDateTime.now();

        ItemRequestDto dto1 = new ItemRequestDto(1L, "Request 1", now, List.of());
        ItemRequestDto dto2 = new ItemRequestDto(2L, "Request 2", now, List.of());

        Mockito.when(requestService.getRequestsByUserId(userId)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Request 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Request 2")));
    }


    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        long requestId = 5L;
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto dto = new ItemRequestDto(requestId, "Need something", now, List.of());

        Mockito.when(requestService.getRequestById(requestId)).thenReturn(dto);

        mockMvc.perform(get("/requests/{id}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) requestId)))
                .andExpect(jsonPath("$.description", is("Need something")));
    }

}
