package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exceptions.ServerErrorException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(user);
    }

    @Test
    void testInputValidatorThrowsOnNullStart() {
        Booking booking = new Booking();
        booking.setEndTime(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        Exception e = assertThrows(ServerErrorException.class,
                () -> invokeInputValidator(booking));
        assertTrue(e.getMessage().contains("must be set"));
    }

    @Test
    void testInputValidatorThrowsOnStartAfterEnd() {
        Booking booking = new Booking();
        booking.setStartTime(LocalDateTime.now().plusDays(2));
        booking.setEndTime(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        Exception e = assertThrows(ServerErrorException.class,
                () -> invokeInputValidator(booking));
        assertTrue(e.getMessage().contains("must be after"));
    }

    @Test
    void testInputValidatorPasses() {
        Booking booking = new Booking();
        booking.setStartTime(LocalDateTime.now().plusMinutes(1));
        booking.setEndTime(LocalDateTime.now().plusHours(2));
        booking.setItem(item);

        assertDoesNotThrow(() -> invokeInputValidator(booking));
    }

    @Test
    void testGetAllBookingsStateFilter() {
        Booking booking = new Booking();
        booking.setStartTime(LocalDateTime.now().minusHours(1));
        booking.setEndTime(LocalDateTime.now().plusHours(1));
        booking.setStatus(BookingState.APPROVED);
        booking.setItem(item);
        booking.setUser(user);

        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(booking));

        for (String state : List.of("WAITING", "APPROVED", "REJECTED", "CURRENT", "PAST", "FUTURE", "ALL", "UNKNOWN")) {
            List<BookingDtoResponse> result = bookingService.getAllBookings(1L, state);
            assertNotNull(result);
        }
    }

    private void invokeInputValidator(Booking booking) {
        try {
            Method method = BookingServiceImpl.class.getDeclaredMethod("inputValidator", Booking.class);
            method.setAccessible(true);
            method.invoke(bookingService, booking);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
