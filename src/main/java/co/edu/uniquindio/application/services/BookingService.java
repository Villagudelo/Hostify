package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.booking.BookingDTO;
import co.edu.uniquindio.application.dto.booking.CreateBookingDTO;
import co.edu.uniquindio.application.dto.booking.ItemBookingDTO;
import co.edu.uniquindio.application.dto.booking.StatusBookingDTO;
import co.edu.uniquindio.application.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    Long create(CreateBookingDTO createBookingDTO, String email) throws Exception;

    void changeStatus(Long id, StatusBookingDTO statusBookingDTO) throws Exception;

    BookingDTO getById(Long id) throws Exception;

    List<ItemBookingDTO> getBookings(Long placeId) throws Exception;

    void cancelBooking(Long bookingId, String email) throws Exception;

    void approveBooking(Long bookingId, String hostEmail) throws Exception;
    
    void rejectBooking(Long bookingId, String hostEmail) throws Exception;

    List<ItemBookingDTO> getBookingsUser(String email, BookingStatus status, int page, int size) throws Exception;

    List<ItemBookingDTO> getBookingsByPlace(Long placeId, BookingStatus status, LocalDateTime from, LocalDateTime to, String hostEmail, int page, int size) throws Exception;

    void updateStatus(Long bookingId, BookingStatus newStatus, String userEmail) throws Exception;

}
