package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.entity.Comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByBookingId(Long bookingId);

    List<Comment> findByPlaceId(Long placeId);
}
