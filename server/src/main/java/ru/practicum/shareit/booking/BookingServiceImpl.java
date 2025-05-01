package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ServerErrorException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    public List<BookingDtoResponse> getAllBookings(long userId, String noConvertedState) {
        BookingState state;
        try {
            state = BookingState.valueOf(noConvertedState);
        } catch (IllegalArgumentException e) {
            state = BookingState.ALL;
        }
        LocalDateTime now = LocalDateTime.now();
        BookingState finalState = state;
        return bookingRepository.findByUserId(userId).stream()
                .filter(booking -> {
                    switch (finalState) {
                        case WAITING:
                            return booking.getStatus() == BookingState.WAITING;
                        case APPROVED:
                            return booking.getStatus() == BookingState.APPROVED;
                        case REJECTED:
                            return booking.getStatus() == BookingState.REJECTED;
                        case CURRENT:
                            return !booking.getStartTime().isAfter(now) && !booking.getEndTime().isBefore(now);
                        case PAST:
                            return booking.getEndTime().isBefore(now);
                        case FUTURE:
                            return booking.getStartTime().isAfter(now);
                        case ALL:
                        default:
                            return true;
                    }
                })
                .sorted(Comparator.comparing(Booking::getStartTime).reversed())
                .map(BookingMapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDtoResponse getBookingById(long bookingId, long userId) {
        return BookingMapper.toDtoResponse(bookingRepository.getBookingById(bookingId));
    }

    public BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, long userId) {
        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingDtoRequest.getItemId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Booking booking = bookingMapper.fromDto(bookingDtoRequest, item, user);
        booking.setStatus(BookingState.WAITING);
        inputValidator(booking);

        return BookingMapper.toDtoResponse(bookingRepository.save(booking));
    }

    public BookingDtoResponse approveBooking(long bookingId, long userId, boolean state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException("User not found with id: " + userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getItem().getOwner().equals(user)) {
            throw new ServerErrorException("You are not owner of this booking");
        }
        booking.setStatus(state ? BookingState.APPROVED : BookingState.REJECTED);
        return BookingMapper.toDtoResponse(bookingRepository.save(booking));
    }

    private void inputValidator(Booking booking) {

        if (booking.getStartTime() == null || booking.getEndTime() == null) {
            throw new ServerErrorException("Booking start time and end time must be set");
        }
        if (booking.getStartTime().isAfter(booking.getEndTime())) {
            throw new ServerErrorException("Booking start time must be after end time");
        }
        if (booking.getStartTime().equals(booking.getEndTime())) {
            throw new ServerErrorException("Booking start must not be the same time");
        }

        LocalDateTime now = LocalDateTime.now();
        if (booking.getStartTime().isBefore(now)) {
            throw new ServerErrorException("Booking start time must be in the future or present");
        }


        if (!booking.getItem().getAvailable()) {
            throw new ServerErrorException("Booking is not available");
        }
    }
}
