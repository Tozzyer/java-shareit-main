package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setId(itemRequest.getOwner().getId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }
    public static ItemRequest fromDto(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setOwner(user);
        return itemRequest;
    }
}
