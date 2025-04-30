package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemForRequestListDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestMapperTest {

    private ItemRepository itemRepository;
    private ItemRequestMapper mapper;

    @BeforeEach
    void setup() {
        itemRepository = mock(ItemRepository.class);
        mapper = new ItemRequestMapper(itemRepository);
    }

    @Test
    void toDto_shouldMapCorrectly() {
        User owner = new User();
        owner.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(42L);
        request.setDescription("Need a laptop");
        request.setCreated(LocalDateTime.of(2023, 1, 1, 12, 0));
        request.setOwner(owner);

        Item item = new Item();
        item.setId(100L);
        item.setName("Laptop");
        item.setDescription("Old but gold");
        item.setOwner(owner);
        item.setItemRequest(request);

        when(itemRepository.findByItemRequestId(42L)).thenReturn(List.of(item));

        ItemRequestDto dto = mapper.toDto(request);

        assertEquals(42L, dto.getId());
        assertEquals("Need a laptop", dto.getDescription());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), dto.getCreated());

        assertEquals(1, dto.getItems().size());
        ItemForRequestListDto dtoItem = dto.getItems().get(0);
        assertEquals(100L, dtoItem.getId());
        assertEquals("Laptop", dtoItem.getName());
        assertEquals("Old but gold", dtoItem.getDescription());
        assertEquals(1L, dtoItem.getOwnerId());
    }

    @Test
    void fromDto_shouldMapToEntityCorrectly() {
        User user = new User();
        user.setId(77L);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need a vacuum cleaner");

        ItemRequest entity = mapper.fromDto(dto, user);
        assertEquals("Need a vacuum cleaner", entity.getDescription());
        assertEquals(user, entity.getOwner());
    }

    @Test
    void fromDtoSupport_shouldMapItemCorrectly() {
        User owner = new User();
        owner.setId(5L);

        Item item = new Item();
        item.setId(10L);
        item.setName("Chair");
        item.setDescription("Wooden chair");
        item.setOwner(owner);

        ItemForRequestListDto dto = ItemRequestMapper.fromDtoSupport(item);

        assertEquals(10L, dto.getId());
        assertEquals("Chair", dto.getName());
        assertEquals("Wooden chair", dto.getDescription());
        assertEquals(5L, dto.getOwnerId());
    }
}
