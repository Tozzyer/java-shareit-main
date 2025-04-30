package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemMapperTest {

    private ItemMapper itemMapper;
    private CommentRepository commentRepository;
    private ItemRequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        commentRepository = Mockito.mock(CommentRepository.class);
        requestRepository = Mockito.mock(ItemRequestRepository.class);
        itemMapper = new ItemMapper(commentRepository, requestRepository);
    }

    @Test
    void fromDto_shouldMapItemDtoToItem() {
        // given
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Electric drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(100L);

        ItemRequest mockRequest = new ItemRequest();
        mockRequest.setId(100L);

        when(requestRepository.getRequestsById(100L)).thenReturn(mockRequest);
        Item result = itemMapper.fromDto(itemDto);
        assertEquals("Drill", result.getName());
        assertEquals("Electric drill", result.getDescription());
        assertTrue(result.getAvailable());
        assertNotNull(result.getItemRequest());
        assertEquals(100L, result.getItemRequest().getId());
    }

    @Test
    void toDto_shouldMapItemToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Electric drill");
        item.setAvailable(true);

        ItemRequest request = new ItemRequest();
        request.setId(200L);
        item.setItemRequest(request);

        when(commentRepository.findByItemId(1L)).thenReturn(Collections.emptyList());
        ItemDto result = itemMapper.toDto(item);
        assertEquals(1L, result.getId());
        assertEquals("Drill", result.getName());
        assertEquals("Electric drill", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(200L, result.getRequestId());
        assertNotNull(result.getComments());
        assertEquals(0, result.getComments().size());
    }
}
