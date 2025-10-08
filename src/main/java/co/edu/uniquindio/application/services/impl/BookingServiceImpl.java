package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.booking.*;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.model.entity.*;
import co.edu.uniquindio.application.model.enums.BookingStatus;
import co.edu.uniquindio.application.repositories.*;
import co.edu.uniquindio.application.services.BookingService;
import co.edu.uniquindio.application.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public void create(CreateBookingDTO createBookingDTO, String email) throws Exception {
        Place place = placeRepository.findById(createBookingDTO.placeId())
                .orElseThrow(() -> new ValidationException("Alojamiento no encontrado"));

        User guest = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        LocalDateTime checkIn = createBookingDTO.checkIn().atStartOfDay();
        LocalDateTime checkOut = createBookingDTO.checkOut().atStartOfDay();

        // Validaciones
        if (checkIn.isBefore(LocalDateTime.now()) || checkOut.isBefore(LocalDateTime.now())) {
            throw new ValidationException("No se pueden reservar fechas pasadas");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("La reserva debe ser mínimo de una noche");
        }
        if (createBookingDTO.guestCount() > place.getMaxGuests()) {
            throw new ValidationException("No se puede superar la capacidad máxima");
        }

        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                place.getId(), checkIn, checkOut);
        if (!overlapping.isEmpty()) {
            throw new ValidationException("El alojamiento no está disponible en esas fechas");
        }

        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setPlace(place);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuestCount(createBookingDTO.guestCount());

        bookingRepository.save(booking);
        emailService.sendBookingConfirmation(guest.getEmail(), booking);
    }

    @Override
    public void cancelBooking(Long bookingId, String email) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException("Reserva no encontrada"));

        // Validar que la reserva pertenece al usuario autenticado
        if (!booking.getGuest().getEmail().equals(email)) {
            throw new ValidationException("No puedes cancelar reservas de otros usuarios");
        }

        // Validar política de cancelación (mínimo 48h antes del check-in)
        LocalDateTime now = LocalDateTime.now();
        if (booking.getCheckIn().minusHours(48).isBefore(now)) {
            throw new ValidationException("Solo puedes cancelar reservas hasta 48 horas antes del check-in");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public void changeStatus(Long id, StatusBookingDTO statusBookingDTO) throws Exception {

    }

    @Override
    public BookingDTO getById(Long id) throws Exception {
        return null;
    }

    @Override
    public List<ItemBookingDTO> getBookings(Long placeId) throws Exception {
        return List.of();
    }

    @Override
    public List<ItemBookingDTO> getBookingsUser(String email, BookingStatus status, int page, int size) throws Exception {
        Page<Booking> bookings = bookingRepository.findBookingsByUserAndStatusPaged(email, status, PageRequest.of(page, size));
        return bookings.stream().map(b -> new ItemBookingDTO(
                b.getId(),
                b.getPlace().getId(),
                b.getPlace().getTitle(),
                b.getPlace().getCity(),
                b.getCreatedAt(),
                b.getCheckIn(),
                b.getCheckOut(),
                b.getGuestCount(),
                b.getPlace().getPrice(),
                b.getStatus().name()
        )).toList();
    }

    @Override
    public List<ItemBookingDTO> getBookingsByPlace(
            Long placeId,
            BookingStatus status,
            LocalDateTime from,
            LocalDateTime to,
            String hostEmail,
            int page,
            int size
    ) throws Exception {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new NotFoundException("Alojamiento no encontrado"));

        if (!place.getHost().getEmail().equals(hostEmail)) {
            throw new ValidationException("Solo el anfitrión puede ver las reservas de su alojamiento");
        }

        Page<Booking> bookings = bookingRepository.findBookingsByPlaceAndFiltersPaged(placeId, status, from, to, PageRequest.of(page, size));

        return bookings.stream().map(b -> new ItemBookingDTO(
            b.getId(),
            b.getPlace().getId(),
            b.getPlace().getTitle(),
            b.getPlace().getCity(),
            b.getCreatedAt(),
            b.getCheckIn(),
            b.getCheckOut(),
            b.getGuestCount(),
            b.getPlace().getPrice(),
            b.getStatus().name()
        )).toList();
    }

    @Override
    public void approveBooking(Long bookingId, String hostEmail) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

        // Validar que el usuario autenticado es el anfitrión
        if (!booking.getPlace().getHost().getEmail().equals(hostEmail)) {
            throw new ValidationException("Solo el anfitrión puede aprobar la reserva");
        }

        if (!booking.getStatus().equals(BookingStatus.PENDING)) {
            throw new ValidationException("Solo reservas pendientes pueden ser aprobadas");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    @Override
    public void rejectBooking(Long bookingId, String hostEmail) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

        if (!booking.getPlace().getHost().getEmail().equals(hostEmail)) {
            throw new ValidationException("Solo el anfitrión puede rechazar la reserva");
        }

        if (!booking.getStatus().equals(BookingStatus.PENDING)) {
            throw new ValidationException("Solo reservas pendientes pueden ser rechazadas");
        }

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
    }
}
