package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.InputDataErrorException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Nice item!");
        comment.setItem(item);
        comment.setAuthorName(user.getName());
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Nice item!");
        commentDto.setAuthorName(user.getName());
    }

    @Test
    void createItem_UserExists_ReturnsItemDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemMapper.fromDto(itemDto)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.createItem(itemDto, user.getId());

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        verify(userRepository).findById(user.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, user.getId()));
        verify(userRepository).findById(user.getId());
        verifyNoInteractions(itemRepository);
    }

    @Test
    void createComment_ValidBooking_ReturnsCommentDto() {
        Booking booking = new Booking();
        booking.setItem(item);

        when(bookingRepository.findByUserIdAndStatusAndEndTimeBeforeOrderByEndTimeDesc(eq(user.getId()), eq(BookingState.APPROVED), any()))
                .thenReturn(List.of(booking));
        when(commentMapper.fromDto(commentDto)).thenReturn(comment);
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(itemRepository.getById(item.getId())).thenReturn(item);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(commentDto, user.getId(), item.getId());

        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_NoValidBooking_ThrowsBadRequestException() {
        when(bookingRepository.findByUserIdAndStatusAndEndTimeBeforeOrderByEndTimeDesc(eq(user.getId()), eq(BookingState.APPROVED), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> itemService.createComment(commentDto, user.getId(), item.getId()));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getItem_ItemExists_ReturnsItemDto() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.getItem(item.getId());

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        verify(itemRepository).findById(item.getId());
    }

    @Test
    void getItem_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(item.getId()));
    }

    @Test
    void getAllItemsFromUser_ReturnsListOfItemDto() {
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getAllItemsFromUser(user.getId());

        assertEquals(1, result.size());
        verify(itemRepository).findByOwnerId(user.getId());
    }

    @Test
    void searchItems_WithNonEmptySearch_ReturnsAvailableItems() {
        when(itemRepository.findByNameContainingIgnoreCase("item")).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItems("item");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailable());
    }

    @Test
    void searchItems_WithEmptySearch_ReturnsEmptyList() {
        List<ItemDto> result = itemService.searchItems("");

        assertTrue(result.isEmpty());
        verifyNoInteractions(itemRepository);
    }

    @Test
    void updateItem_ItemExistsAndBelongsToUser_UpdatesItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto updated = itemService.updateItem(item.getId(), user.getId(), itemDto);

        assertNotNull(updated);
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_ItemNotBelongingToUser_ThrowsInputDataErrorException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        item.setOwner(anotherUser);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(InputDataErrorException.class, () -> itemService.updateItem(item.getId(), user.getId(), itemDto));
    }

    @Test
    void updateItem_ItemNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(item.getId(), user.getId(), itemDto));
    }
}
