package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemForRequestListDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemRequestMapper {

    private final ItemRepository itemRepository;

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        List<ItemForRequestListDto> itemForRequestListDto = itemRepository.findByItemRequestId(itemRequest.getId()).stream()
                .map(ItemRequestMapper::fromDtoSupport)
                .collect(Collectors.toList());
        itemRequestDto.setItems(itemForRequestListDto);
        return itemRequestDto;
    }

    public ItemRequest fromDto(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setOwner(user);
        return itemRequest;
    }

    public static ItemForRequestListDto fromDtoSupport(Item item) {
        ItemForRequestListDto itemForRequestListDto = new ItemForRequestListDto();
        itemForRequestListDto.setName(item.getName());
        itemForRequestListDto.setId(item.getId());
        itemForRequestListDto.setDescription(item.getDescription());
        itemForRequestListDto.setOwnerId(item.getOwner().getId());
        return itemForRequestListDto;
    }

}
