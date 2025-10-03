package co.edu.uniquindio.application.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingDTO(
        @NotNull @Future LocalDate checkIn,
        @NotNull @Future LocalDate checkOut,
        @NotNull Long placeId,
        @NotNull int guestCount
) {
}
