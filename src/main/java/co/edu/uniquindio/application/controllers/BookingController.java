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
import java.time.LocalDateTime;
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
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        String email = principal.getName();
        List<ItemBookingDTO> bookings = bookingService.getBookingsUser(email, status, page, size);
        return ResponseEntity.ok(new ResponseDTO<>(false, bookings));
}

    @GetMapping("/place/{placeId}")
    public ResponseEntity<ResponseDTO<List<ItemBookingDTO>>> getBookingsByPlace(
            @PathVariable Long placeId,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) throws Exception {
        String hostEmail = principal.getName();
        List<ItemBookingDTO> bookings = bookingService.getBookingsByPlace(placeId, status, from, to, hostEmail, page, size);
        return ResponseEntity.ok(new ResponseDTO<>(false, bookings));
    }

    @PatchMapping("/approve/{bookingId}")
    public ResponseEntity<ResponseDTO<String>> approveBooking(
            @PathVariable Long bookingId,
            Principal principal) throws Exception {
        String hostEmail = principal.getName();
        bookingService.approveBooking(bookingId, hostEmail);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Reserva aprobada correctamente"));
    }

    @PatchMapping("/reject/{bookingId}")
    public ResponseEntity<ResponseDTO<String>> rejectBooking(
            @PathVariable Long bookingId,
            Principal principal) throws Exception {
        String hostEmail = principal.getName();
        bookingService.rejectBooking(bookingId, hostEmail);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Reserva rechazada correctamente"));
    }
}