package co.edu.uniquindio.application.dto.booking;

import java.time.LocalDateTime;

public record BookingDTO(
        Long placeId,
        String placeName,
        String placeAddress,
        LocalDateTime createdAt,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        int guestCount,
        float price,
        String status
) {
}
