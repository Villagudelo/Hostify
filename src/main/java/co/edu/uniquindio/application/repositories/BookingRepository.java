package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.entity.Booking;
import co.edu.uniquindio.application.model.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b FROM Booking b
        WHERE b.place.id = :placeId
        AND b.status = co.edu.uniquindio.application.model.enums.BookingStatus.CONFIRMED
        AND (
            (:checkIn BETWEEN b.checkIn AND b.checkOut)
            OR (:checkOut BETWEEN b.checkIn AND b.checkOut)
            OR (b.checkIn BETWEEN :checkIn AND :checkOut)
        )
    """)
    List<Booking> findOverlappingBookings(Long placeId, LocalDateTime checkIn, LocalDateTime checkOut);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.guest.email = :email
        AND (:status IS NULL OR b.status = :status)
        ORDER BY b.checkIn DESC
    """)
    List<Booking> findBookingsByUserAndStatus(String email, co.edu.uniquindio.application.model.enums.BookingStatus status);

        // filepath: src/main/java/co/edu/uniquindio/application/repositories/BookingRepository.java
    List<Booking> findByPlaceIdAndStatus(Long placeId, BookingStatus status);

    @Query("""
        SELECT COUNT(b) FROM Booking b
        WHERE b.place.id = :placeId
        AND b.status = co.edu.uniquindio.application.model.enums.BookingStatus.CONFIRMED
        AND b.checkIn >= :from AND b.checkOut <= :to
    """)
    int countBookingsByPlaceAndDates(Long placeId, LocalDateTime from, LocalDateTime to);
}
