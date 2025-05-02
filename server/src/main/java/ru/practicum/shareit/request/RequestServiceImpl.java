package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;


    @Override
    public ItemRequestDto createRequest(ItemRequestDto dto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = itemRequestMapper.fromDto(dto, user);
        itemRequest.setCreated(now);
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequestsByUserId(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return itemRequestRepository.getRequestsByOwnerId(userId).stream()
                .map(itemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId) {
        return itemRequestRepository.findByOwner_IdNot(userId).stream().map(itemRequestMapper::toDto).toList();
    }

    @Override
    public ItemRequestDto getRequestById(long id) {

        System.out.println(itemRequestRepository.getRequestsById(id));
        return itemRequestMapper.toDto(itemRequestRepository.getRequestsById(id));
    }
}
