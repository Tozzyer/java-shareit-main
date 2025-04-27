package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need a bike");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Need a bike");
    }

    @Test
    void createRequest_UserExists_ReturnsItemRequestDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestMapper.fromDto(itemRequestDto, user)).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = requestService.createRequest(itemRequestDto, user.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(itemRequestDto, user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoInteractions(itemRequestMapper, itemRequestRepository);
    }

    @Test
    void getRequestsByUserId_UserExists_ReturnsListOfItemRequestDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.getRequestsByOwnerId(user.getId())).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> results = requestService.getRequestsByUserId(user.getId());

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(itemRequestDto.getId(), results.get(0).getId());
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).getRequestsByOwnerId(user.getId());
    }

    @Test
    void getRequestsByUserId_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestsByUserId(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getRequestById_ReturnsItemRequestDto() {
        when(itemRequestRepository.getRequestsById(itemRequest.getId())).thenReturn(itemRequest);
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = requestService.getRequestById(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        verify(itemRequestRepository, times(2)).getRequestsById(itemRequest.getId());
        verify(itemRequestMapper, times(1)).toDto(itemRequest);
    }
}
