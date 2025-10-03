package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.booking.BookingDTO;
import co.edu.uniquindio.application.dto.booking.CreateBookingDTO;
import co.edu.uniquindio.application.dto.booking.ItemBookingDTO;
import co.edu.uniquindio.application.dto.booking.StatusBookingDTO;
import co.edu.uniquindio.application.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Override
    public void create(CreateBookingDTO createBookingDTO) throws Exception {

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
