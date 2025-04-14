package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemServiceImpl;
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);


    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        log.info("Creating new item: " + itemDto);
        return itemServiceImpl.createItem(itemDto, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        log.info("Getting item: " + id);
        return itemServiceImpl.getItem(id);
    }

    @GetMapping
    public List<ItemDto> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting all items from user: " + userId);
        return itemServiceImpl.getAllItemsFromUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam("text") String search) {
        log.info("Searching for: " + search);
        return itemServiceImpl.searchItems(search);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable("id") long id, @RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        log.info("Updating item: " + itemDto);
        return itemServiceImpl.updateItem(id, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto, @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating new comment: " + commentDto);
        return itemServiceImpl.createComment(commentDto, userId, itemId);
    }
}
