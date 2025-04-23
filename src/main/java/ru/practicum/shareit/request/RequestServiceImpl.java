package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    ItemRequestRepository itemRequestRepository;
    ItemRequestMapper itemRequestMapper;
    UserRepository userRepository;


    @Override
    public ItemRequestDto createRequest(ItemRequestDto dto, long userId) {
                User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequestMapper.fromDto(dto, user);
        itemRequest.setCreated(now);
        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequestsByUserId(long userId) {
        return List.of();
    }

    @Override
    public ItemRequestDto getRequestById(long id) {
        return null;
    }
}
