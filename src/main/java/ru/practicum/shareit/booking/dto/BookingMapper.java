package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
@AllArgsConstructor
public class BookingMapper {

    public Booking fromDto(BookingDtoRequest bookingDtoRequest, Item item, User user) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setItem(item);
        booking.setStartTime(bookingDtoRequest.getStart());
        booking.setEndTime(bookingDtoRequest.getEnd());
        return booking;
    }


    public static BookingDtoResponse toDtoResponse(Booking booking) {
        return new BookingDtoResponse(booking.getId(),
                booking.getItem(),
                booking.getUser(),
                booking.getStatus(),
                booking.getStartTime(),
                booking.getEndTime());
    }
}
