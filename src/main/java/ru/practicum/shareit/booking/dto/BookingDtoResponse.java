package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoResponse {
    private long id;
    private Item item;
    private User booker;
    private BookingState status;
    private LocalDateTime start;
    private LocalDateTime end;
}
