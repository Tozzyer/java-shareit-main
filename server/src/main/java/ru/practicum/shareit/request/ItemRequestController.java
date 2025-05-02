package ru.practicum.shareit.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static Logger log = LoggerFactory.getLogger(ItemRequestController.class);

    private final RequestService requestService;

    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemRequestDto requestDto) {
        log.info("Creating new request: " + requestDto);
        return requestService.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requests by userId: " + userId);
        return requestService.getRequestsByUserId(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@PathVariable long id) {
        log.info("Get request by id: " + id);
        System.out.println("getRequestById" + id);
        return requestService.getRequestById(id);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllItemRequests(userId);
    }

}
