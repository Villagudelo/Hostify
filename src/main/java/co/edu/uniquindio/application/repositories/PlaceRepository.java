package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.entity.Place;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("""
        SELECT p FROM Place p
        WHERE p.status = 'ACTIVE'
        AND (:city IS NULL OR p.city = :city)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:services IS NULL OR p.services IN :services)
        AND NOT EXISTS (
            SELECT b FROM Booking b
            WHERE b.place.id = p.id
            AND b.status = 'CONFIRMED'
            AND (
                (:checkIn BETWEEN b.checkIn AND b.checkOut)
                OR (:checkOut BETWEEN b.checkIn AND b.checkOut)
                OR (b.checkIn BETWEEN :checkIn AND :checkOut)
            )
        )
    """)

    List<Place> searchAvailablePlaces(
        @Param("city") String city,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("services") List<String> services
    );

}
