package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class ItemRequestDto {
    @NotBlank
    private long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestListDto> items;
}
