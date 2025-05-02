package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsForUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequestsFromUser(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getItemRequest(@PathVariable long id) {
        return itemRequestClient.getItemRequest(id);
    }
}

