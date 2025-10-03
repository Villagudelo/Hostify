package co.edu.uniquindio.application.dto.booking;

import co.edu.uniquindio.application.model.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record StatusBookingDTO(
        @NotNull BookingStatus newStatus
) {
}
