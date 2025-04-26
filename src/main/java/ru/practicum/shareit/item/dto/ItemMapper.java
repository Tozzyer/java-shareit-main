package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemMapper {

    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    public Item fromDto(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setItemRequest(requestRepository.getRequestsById(itemDto.getRequestId()));
        return item;
    }

    public ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setComments(commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList()));
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        if(item.getItemRequest() != null) { itemDto.setRequestId(item.getItemRequest().getId()); }
        return itemDto;
    }
}
