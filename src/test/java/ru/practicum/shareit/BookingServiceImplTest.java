package ru.practicum.shareit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ServerErrorException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.Optional;

public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDtoRequest bookingDtoRequest;
    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        item = new Item();
        item.setId(2L);
        item.setName("Test Item");
        item.setDescription("Test Item Description");
        item.setAvailable(true);
        item.setOwner(user);
        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setUser(user);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingState.WAITING);

        bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_ValidBooking_ReturnsBookingDtoResponse() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.fromDto(bookingDtoRequest, item, user)).thenReturn(booking);

        BookingDtoResponse response = bookingService.createBooking(bookingDtoRequest, 1L);

        assertNotNull(response);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createBooking_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoRequest, 1L);
        });
    }

    @Test
    void createBooking_UserNotFound_ThrowsNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoRequest, 1L);
        });
    }

    @Test
    void approveBooking_ValidBooking_ReturnsBookingDtoResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse response = bookingService.approveBooking(1L, 1L, true);

        assertNotNull(response);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void approveBooking_UserNotOwner_ThrowsServerErrorException() {
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setName("Different User");
        differentUser.setEmail("different@example.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(differentUser));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ServerErrorException.class, () -> {
            bookingService.approveBooking(1L, 2L, true);
        });
    }

    @Test
    void approveBooking_BookingNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(1L, 1L, true);
        });
    }

    @Test
    void inputValidator_InvalidStartTime_ThrowsServerErrorException() {
        booking.setStartTime(LocalDateTime.now().plusDays(2));
        booking.setEndTime(LocalDateTime.now().plusDays(1));

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoRequest, 1L);
        });
    }
}
