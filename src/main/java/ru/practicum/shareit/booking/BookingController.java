package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    BookingServiceImpl bookingService;
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("BookingDtoRequest: {}", bookingDtoRequest);
        return bookingService.createBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approveBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(name = "approved") boolean approved) {
        log.info("BookingDtoRequest: {}", bookingId);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("BookingDtoRequest: {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookings(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("BookingDtoRequest: {}", userId);
        return bookingService.getAllBookings(userId, state);
    }
}
