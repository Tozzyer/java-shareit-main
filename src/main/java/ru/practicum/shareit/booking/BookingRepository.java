package ru.practicum.shareit.booking;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    public Booking getBookingById(long id);

    List<Booking> findByUserId(Long userId);

    public Booking getBookingByUserIdAndItemId(long userId, long itemId);

    public List<Booking> findByUserIdAndStatusAndEndTimeBeforeOrderByEndTimeDesc(long userId, BookingState state, LocalDateTime endBefore);
}
