package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.InputDataErrorException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ServerErrorException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShareItTests {

    private UserMapper userMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;
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

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @InjectMocks
    private RequestServiceImpl requestService;

    @InjectMocks
    private UserService userService;

    //ForRequests
    private User userRequestTestVar;
    private ItemRequest itemRequestTestVar;
    private ItemRequestDto itemRequestDtoTestVar;

    //ForService
    private User userForService;
    private Item itemForService;
    private ItemDto itemDtoForService;
    private Comment commentForService;
    private CommentDto commentDtoForService;

    //ForBooking
    private BookingDtoRequest bookingDtoRequest;
    private User user;
    private Item item;
    private Booking booking;


    @BeforeEach
    void setUp() {
        //ForRequests
        userRequestTestVar = new User();
        userRequestTestVar.setId(1L);
        userRequestTestVar.setName("Test User");
        userRequestTestVar.setEmail("test@example.com");

        itemRequestTestVar = new ItemRequest();
        itemRequestTestVar.setId(1L);
        itemRequestTestVar.setDescription("Need a bike");

        itemRequestDtoTestVar = new ItemRequestDto();
        itemRequestDtoTestVar.setId(1L);
        itemRequestDtoTestVar.setDescription("Need a bike");

        //ForService
        userForService = new User();
        userForService.setId(1L);
        userForService.setName("User");
        userForService.setEmail("user@example.com");

        itemForService = new Item();
        itemForService.setId(1L);
        itemForService.setName("Item");
        itemForService.setDescription("Item description");
        itemForService.setAvailable(true);
        itemForService.setOwner(userForService);

        itemDtoForService = new ItemDto();
        itemDtoForService.setId(1L);
        itemDtoForService.setName("Item");
        itemDtoForService.setDescription("Item description");
        itemDtoForService.setAvailable(true);

        commentForService = new Comment();
        commentForService.setId(1L);
        commentForService.setText("Nice item!");
        commentForService.setItem(itemForService);
        commentForService.setAuthorName(userForService.getName());
        commentForService.setCreated(LocalDateTime.now());

        commentDtoForService = new CommentDto();
        commentDtoForService.setId(1L);
        commentDtoForService.setText("Nice item!");
        commentDtoForService.setAuthorName(userForService.getName());
        //BookingTestSetup
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        item = new Item();
        item.setId(2L);
        item.setName("Test Item");
        item.setDescription("Test Item Description");
        item.setAvailable(true);
        item.setOwner(user);
        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setUser(user);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingState.WAITING);

        bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(2));
        userMapper = new UserMapper();
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void createItem_UserExists_ReturnsItemDto() {
        when(userRepository.findById(userForService.getId())).thenReturn(Optional.of(userForService));
        when(itemMapper.fromDto(itemDtoForService)).thenReturn(itemForService);
        when(itemRepository.save(any(Item.class))).thenReturn(itemForService);
        when(itemMapper.toDto(itemForService)).thenReturn(itemDtoForService);

        ItemDto result = itemService.createItem(itemDtoForService, userForService.getId());

        assertNotNull(result);
        assertEquals(itemDtoForService.getId(), result.getId());
        verify(userRepository).findById(userForService.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(userForService.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDtoForService, userForService.getId()));
        verify(userRepository).findById(userForService.getId());
        verifyNoInteractions(itemRepository);
    }

    @Test
    void createComment_ValidBooking_ReturnsCommentDto() {
        Booking booking = new Booking();
        booking.setItem(itemForService);

        when(bookingRepository.findByUserIdAndStatusAndEndTimeBeforeOrderByEndTimeDesc(eq(userForService.getId()), eq(BookingState.APPROVED), any()))
                .thenReturn(List.of(booking));
        when(commentMapper.fromDto(commentDtoForService)).thenReturn(commentForService);
        when(userRepository.getById(userForService.getId())).thenReturn(userForService);
        when(itemRepository.getById(itemForService.getId())).thenReturn(itemForService);
        when(commentRepository.save(any(Comment.class))).thenReturn(commentForService);

        CommentDto result = itemService.createComment(commentDtoForService, userForService.getId(), itemForService.getId());

        assertNotNull(result);
        assertEquals(commentDtoForService.getText(), result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_NoValidBooking_ThrowsBadRequestException() {
        when(bookingRepository.findByUserIdAndStatusAndEndTimeBeforeOrderByEndTimeDesc(eq(userForService.getId()), eq(BookingState.APPROVED), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> itemService.createComment(commentDtoForService, userForService.getId(), itemForService.getId()));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getItem_ItemExists_ReturnsItemDto() {
        when(itemRepository.findById(itemForService.getId())).thenReturn(Optional.of(itemForService));
        when(itemMapper.toDto(itemForService)).thenReturn(itemDtoForService);

        ItemDto result = itemService.getItem(itemForService.getId());

        assertNotNull(result);
        assertEquals(itemDtoForService.getId(), result.getId());
        verify(itemRepository).findById(itemForService.getId());
    }

    @Test
    void getItem_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepository.findById(itemForService.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemForService.getId()));
    }

    @Test
    void getAllItemsFromUser_ReturnsListOfItemDto() {
        when(itemRepository.findByOwnerId(userForService.getId())).thenReturn(List.of(itemForService));
        when(itemMapper.toDto(itemForService)).thenReturn(itemDtoForService);

        List<ItemDto> result = itemService.getAllItemsFromUser(userForService.getId());

        assertEquals(1, result.size());
        verify(itemRepository).findByOwnerId(userForService.getId());
    }

    @Test
    void searchItems_WithNonEmptySearch_ReturnsAvailableItems() {
        when(itemRepository.findByNameContainingIgnoreCase("item")).thenReturn(List.of(itemForService));
        when(itemMapper.toDto(itemForService)).thenReturn(itemDtoForService);

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
        when(userRepository.findById(userForService.getId())).thenReturn(Optional.of(userForService));
        when(itemRepository.findById(itemForService.getId())).thenReturn(Optional.of(itemForService));
        when(itemMapper.toDto(itemForService)).thenReturn(itemDtoForService);

        ItemDto updated = itemService.updateItem(itemForService.getId(), userForService.getId(), itemDtoForService);

        assertNotNull(updated);
        verify(itemRepository).save(itemForService);
    }

    @Test
    void updateItem_ItemNotBelongingToUser_ThrowsInputDataErrorException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        itemForService.setOwner(anotherUser);

        when(userRepository.findById(userForService.getId())).thenReturn(Optional.of(userForService));
        when(itemRepository.findById(itemForService.getId())).thenReturn(Optional.of(itemForService));

        assertThrows(InputDataErrorException.class, () -> itemService.updateItem(itemForService.getId(), userForService.getId(), itemDtoForService));
    }

    @Test
    void updateItem_ItemNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(userForService.getId())).thenReturn(Optional.of(userForService));
        when(itemRepository.findById(itemForService.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemForService.getId(), userForService.getId(), itemDtoForService));
    }


    @Test
    void createBooking_ValidBooking_ReturnsBookingDtoResponse() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.fromDto(bookingDtoRequest, item, user)).thenReturn(booking);

        BookingDtoResponse response = bookingService.createBooking(bookingDtoRequest, 1L);

        assertNotNull(response);
        verify(bookingRepository, times(1)).save(booking);
    }


    @Test
    void createBooking_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoRequest, 1L);
        });
    }

    @Test
    void createBooking_UserNotFound_ThrowsNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoRequest, 1L);
        });
    }

    @Test
    void approveBooking_ValidBooking_ReturnsBookingDtoResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse response = bookingService.approveBooking(1L, 1L, true);

        assertNotNull(response);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void approveBooking_UserNotOwner_ThrowsServerErrorException() {
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setName("Different User");
        differentUser.setEmail("different@example.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(differentUser));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ServerErrorException.class, () -> {
            bookingService.approveBooking(1L, 2L, true);
        });
    }

    @Test
    void approveBooking_BookingNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(1L, 1L, true);
        });
    }

    @Test
    void inputValidator_InvalidStartTime_ThrowsServerErrorException() {
        booking.setStartTime(LocalDateTime.now().plusDays(2));
        booking.setEndTime(LocalDateTime.now().plusDays(1));

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoRequest, 1L);
        });
    }


    @Test
    void createRequest_UserExists_ReturnsItemRequestDto() {
        when(userRepository.findById(userRequestTestVar.getId())).thenReturn(Optional.of(userRequestTestVar));
        when(itemRequestMapper.fromDto(itemRequestDtoTestVar, userRequestTestVar)).thenReturn(itemRequestTestVar);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequestTestVar);
        when(itemRequestMapper.toDto(itemRequestTestVar)).thenReturn(itemRequestDtoTestVar);

        ItemRequestDto result = requestService.createRequest(itemRequestDtoTestVar, userRequestTestVar.getId());

        assertNotNull(result);
        assertEquals(itemRequestDtoTestVar.getId(), result.getId());
        verify(userRepository, times(1)).findById(userRequestTestVar.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(userRequestTestVar.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(itemRequestDtoTestVar, userRequestTestVar.getId()));
        verify(userRepository, times(1)).findById(userRequestTestVar.getId());
        verifyNoInteractions(itemRequestMapper, itemRequestRepository);
    }

    @Test
    void getRequestsByUserId_UserExists_ReturnsListOfItemRequestDto() {
        when(userRepository.findById(userRequestTestVar.getId())).thenReturn(Optional.of(userRequestTestVar));
        when(itemRequestRepository.getRequestsByOwnerId(userRequestTestVar.getId())).thenReturn(List.of(itemRequestTestVar));
        when(itemRequestMapper.toDto(itemRequestTestVar)).thenReturn(itemRequestDtoTestVar);

        List<ItemRequestDto> results = requestService.getRequestsByUserId(userRequestTestVar.getId());

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(itemRequestDtoTestVar.getId(), results.get(0).getId());
        verify(userRepository, times(1)).findById(userRequestTestVar.getId());
        verify(itemRequestRepository, times(1)).getRequestsByOwnerId(userRequestTestVar.getId());
    }

    @Test
    void getRequestsByUserId_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(userRequestTestVar.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestsByUserId(userRequestTestVar.getId()));
        verify(userRepository, times(1)).findById(userRequestTestVar.getId());
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getRequestById_ReturnsItemRequestDto() {
        when(itemRequestRepository.getRequestsById(itemRequestTestVar.getId())).thenReturn(itemRequestTestVar);
        when(itemRequestMapper.toDto(itemRequestTestVar)).thenReturn(itemRequestDtoTestVar);

        ItemRequestDto result = requestService.getRequestById(itemRequestTestVar.getId());

        assertNotNull(result);
        assertEquals(itemRequestDtoTestVar.getId(), result.getId());
        verify(itemRequestRepository, times(2)).getRequestsById(itemRequestTestVar.getId());
        verify(itemRequestMapper, times(1)).toDto(itemRequestTestVar);
    }

    //UserTests

    @Test
    void createUserTest() {
        UserDto userDto = new UserDto(1L, "name", "test@gmail.com");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("name");
        mockUser.setEmail("test@gmail.com");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userRepository.getById(1L)).thenReturn(mockUser);
        User createdUser = userService.createUser(userDto);
        assertEquals(userDto, userMapper.toDto(createdUser));
        User fetchedUser = userService.getUserById(1L);
        assertEquals(userDto, userMapper.toDto(fetchedUser));
    }

    @Test
    void updateUser_SuccessfulUpdateTest() {
        long userId = 1L;
        UserDto updateDto = new UserDto(null, "Updated Name", "updated@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_UserNotFoundTest() {
        long userId = 1L;
        UserDto updateDto = new UserDto(null, "Updated Name", "updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                userService.updateUser(userId, updateDto)
        );

        assertEquals("User with id 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByIdTest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userRepository.getById(userId)).thenReturn(user);

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John", result.getName());

        verify(userRepository, times(1)).getById(userId);
    }

    @Test
    void deleteUserByIdTest() {
        long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}

