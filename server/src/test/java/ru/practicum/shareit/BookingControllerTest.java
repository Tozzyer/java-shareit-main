package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    void setUp() {
        bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(1L);
        booker.setName("Test User");
        booker.setEmail("test@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(booker);

        bookingDtoResponse = new BookingDtoResponse();
        bookingDtoResponse.setId(1L);
        bookingDtoResponse.setItem(item);
        bookingDtoResponse.setBooker(booker);
        bookingDtoResponse.setStatus(BookingState.WAITING);
        bookingDtoResponse.setStart(bookingDtoRequest.getStart());
        bookingDtoResponse.setEnd(bookingDtoRequest.getEnd());
    }

    @Test
    void createBooking_shouldReturnBookingDtoResponse() throws Exception {
        Mockito.when(bookingService.createBooking(any(), eq(1L)))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Test Item"))
                .andExpect(jsonPath("$.booker.name").value("Test User"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        bookingDtoResponse.setStatus(BookingState.APPROVED);

        Mockito.when(bookingService.approveBooking(eq(1L), eq(1L), eq(true)))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getBookingById(1L, 1L))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booker.email").value("test@example.com"));
    }

    @Test
    void getAllBookings_shouldReturnList() throws Exception {
        Mockito.when(bookingService.getAllBookings(1L, "ALL"))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(1L));
    }
}
