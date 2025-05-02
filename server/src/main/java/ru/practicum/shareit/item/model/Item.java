package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "itemrequest_id")
    private ItemRequest itemRequest;
}
