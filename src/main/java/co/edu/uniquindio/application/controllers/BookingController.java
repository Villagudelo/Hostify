package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.booking.CreateBookingDTO;
import co.edu.uniquindio.application.dto.booking.ItemBookingDTO;
import co.edu.uniquindio.application.model.enums.BookingStatus;
import co.edu.uniquindio.application.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<String>> createBooking(
            @RequestBody CreateBookingDTO createBookingDTO,
            Principal principal) throws Exception {
        String email = principal.getName(); // Email del usuario autenticado
        bookingService.create(createBookingDTO, email);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Reserva creada correctamente"));
    }

    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<ResponseDTO<String>> cancelBooking(
            @PathVariable Long bookingId,
            Principal principal) throws Exception {
        String email = principal.getName();
        bookingService.cancelBooking(bookingId, email);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Reserva cancelada correctamente"));
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseDTO<List<ItemBookingDTO>>> getUserBookings(
            Principal principal,
            @RequestParam(required = false) BookingStatus status
    ) throws Exception {
        String email = principal.getName();
        List<ItemBookingDTO> bookings = bookingService.getBookingsUser(email, status);
        return ResponseEntity.ok(new ResponseDTO<>(false, bookings));
    }
}