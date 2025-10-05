package co.edu.uniquindio.application.dto.place;

import java.time.LocalDateTime;

public record AvailabilityDTO(
    LocalDateTime checkIn,
    LocalDateTime checkOut
) {}
