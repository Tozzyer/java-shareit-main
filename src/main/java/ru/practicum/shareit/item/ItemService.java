package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    public ItemDto createItem(ItemDto itemDto, long userId);

    public ItemDto getItem(long id);

    public List<ItemDto> getAllItemsFromUser(long userId);

    public List<ItemDto> searchItems(String search);

    public ItemDto updateItem(long id, long userId, ItemDto itemDto);

    public CommentDto createComment(CommentDto commentDto, long userId, long itemId);

}
