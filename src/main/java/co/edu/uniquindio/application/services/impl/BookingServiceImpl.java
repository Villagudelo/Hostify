package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.booking.*;
import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.model.entity.*;
import co.edu.uniquindio.application.model.enums.BookingStatus;
import co.edu.uniquindio.application.repositories.*;
import co.edu.uniquindio.application.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

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
    public List<ItemBookingDTO> getBookingsUser(String userId) throws Exception {
        return List.of();
    }
}
