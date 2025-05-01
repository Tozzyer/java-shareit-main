package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemForRequestListDto {
    private long id;
    private String name;
    private String description;
    private long ownerId;
}
