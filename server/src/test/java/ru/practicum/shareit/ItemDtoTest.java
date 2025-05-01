package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Hammer");
        itemDto.setDescription("Heavy-duty hammer");
        itemDto.setAvailable(true);
        itemDto.setRequestId(10L);

        CommentDto comment = new CommentDto();
        comment.setId(5L);
        comment.setText("Great tool!");
        itemDto.setComments(List.of(comment));

        BookingDtoResponse lastBooking = new BookingDtoResponse();
        lastBooking.setId(100L);
        BookingDtoResponse nextBooking = new BookingDtoResponse();
        nextBooking.setId(101L);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);

        // Assert
        assertEquals(1L, itemDto.getId());
        assertEquals("Hammer", itemDto.getName());
        assertEquals("Heavy-duty hammer", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(10L, itemDto.getRequestId());
        assertNotNull(itemDto.getComments());
        assertEquals(1, itemDto.getComments().size());
        assertEquals("Great tool!", itemDto.getComments().getFirst().getText());
        assertEquals(100L, itemDto.getLastBooking().getId());
        assertEquals(101L, itemDto.getNextBooking().getId());
    }
}
