package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    public List<ItemRequest> getRequestsByOwnerId(long userId);

    public ItemRequest getRequestsById(long id);

    public List<ItemRequest> findByUserIdNot(long userId);

}
