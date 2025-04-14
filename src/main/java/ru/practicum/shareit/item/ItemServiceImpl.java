package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.InputDataErrorException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;

    @Transactional
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Item item = itemMapper.fromDto(itemDto);
        item.setOwner(owner);
        item = itemRepository.save(item);
        return itemMapper.toDto(item);

    }

    public CommentDto createComment(CommentDto commentDto, long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        Comment comment = commentMapper.fromDto(commentDto);
        comment.setAuthorName(user.getName());
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
//        if (bookingRepository.getBookingByUserIdAndItemId(userId, itemId) != null) {
//            if (bookingRepository.getBookingByUserIdAndItemId(userId, itemId).getStatus() == BookingState.APPROVED) {
//                throw new BadRequestException("You are not owner of this booking");
//            }
//        }

        return commentMapper.toDto(commentRepository.save(comment));
    }

    public ItemDto getItem(long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));
        return itemMapper.toDto(item);
    }

    public List<ItemDto> getAllItemsFromUser(long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String search) {
        if (search == null || search.isEmpty()) {
            return new ArrayList<ItemDto>();
        }
        return itemRepository.findByNameContainingIgnoreCase(search).stream()
                .map(itemMapper::toDto)
                .filter(ItemDto::getAvailable)
                .collect(Collectors.toList());
    }

    public ItemDto updateItem(long id, long userId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return itemRepository.findById(id).map(existingItem -> {
            boolean isUpdated = false;
            if (existingItem.getOwner().getId() != userId) {
                throw new InputDataErrorException("Item with id " + id + " not related to user with id " + userId);
            }
            if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
                existingItem.setName(itemDto.getName());
                isUpdated = true;
            }
            if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
                existingItem.setDescription(itemDto.getDescription());
                isUpdated = true;
            }
            if (itemDto.getAvailable() != null) {
                existingItem.setAvailable(itemDto.getAvailable());
                isUpdated = true;
            }
            if (isUpdated) {
                itemRepository.save(existingItem);
                return itemMapper.toDto(existingItem);
            } else {
                return itemMapper.toDto(existingItem);
            }

        }).orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));

    }
}
