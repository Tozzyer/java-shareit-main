package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, long userId);

    BookingDtoResponse approveBooking(long bookingId, long userId, boolean approved);

    List<BookingDtoResponse> getAllBookings(long userId, String state);

    BookingDtoResponse getBookingById(long bookingId, long userId);
}
