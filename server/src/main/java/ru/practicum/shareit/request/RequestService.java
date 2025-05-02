package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Repository

public interface RequestService {

    public ItemRequestDto createRequest(ItemRequestDto dto, long userId);

    public List<ItemRequestDto> getRequestsByUserId(long userId);

    public ItemRequestDto getRequestById(long id);

    public List<ItemRequestDto> getAllItemRequests(long userId);

}
