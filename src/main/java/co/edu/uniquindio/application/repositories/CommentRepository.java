package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByBookingId(Long bookingId);

    List<Comment> findByPlaceId(Long placeId);

    @Query("""
        SELECT AVG(c.rating) FROM Comment c
        WHERE c.place.id = :placeId
        AND c.createdAt >= :from AND c.createdAt <= :to
    """)
    Double averageRatingByPlaceAndDates(Long placeId, LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT COUNT(c) FROM Comment c
        WHERE c.place.id = :placeId
        AND c.createdAt >= :from AND c.createdAt <= :to
    """)
    int countReviewsByPlaceAndDates(Long placeId, LocalDateTime from, LocalDateTime to);
}
