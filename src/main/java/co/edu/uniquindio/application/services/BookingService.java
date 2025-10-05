package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.booking.BookingDTO;
import co.edu.uniquindio.application.dto.booking.CreateBookingDTO;
import co.edu.uniquindio.application.dto.booking.ItemBookingDTO;
import co.edu.uniquindio.application.dto.booking.StatusBookingDTO;
import co.edu.uniquindio.application.model.enums.BookingStatus;

import java.util.List;

public interface BookingService {

    void create(CreateBookingDTO createBookingDTO, String email) throws Exception;

    void changeStatus(Long id, StatusBookingDTO statusBookingDTO) throws Exception;

    BookingDTO getById(Long id) throws Exception;

    List<ItemBookingDTO> getBookings(Long placeId) throws Exception;

    List<ItemBookingDTO> getBookingsUser(String email, BookingStatus status) throws Exception;   

    void cancelBooking(Long bookingId, String email) throws Exception;

}
