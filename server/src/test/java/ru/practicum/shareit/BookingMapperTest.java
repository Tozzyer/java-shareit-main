package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper();
    }

    @Test
    void fromDto_shouldMapCorrectly() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDtoRequest dtoRequest = new BookingDtoRequest();
        dtoRequest.setStart(start);
        dtoRequest.setEnd(end);
        dtoRequest.setItemId(1L);

        User user = new User();
        user.setId(10L);
        user.setName("User");
        user.setEmail("user@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(user);

        // when
        Booking result = bookingMapper.fromDto(dtoRequest, item, user);

        // then
        assertNotNull(result);
        assertEquals(item, result.getItem());
        assertEquals(user, result.getUser());
        assertEquals(start, result.getStartTime());
        assertEquals(end, result.getEndTime());
    }

    @Test
    void toDtoResponse_shouldMapCorrectly() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User user = new User();
        user.setId(10L);
        user.setName("User");
        user.setEmail("user@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(123L);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setStatus(BookingState.APPROVED);

        BookingDtoResponse dto = BookingMapper.toDtoResponse(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(item, dto.getItem());
        assertEquals(user, dto.getBooker());
        assertEquals(BookingState.APPROVED, dto.getStatus());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
    }
}
