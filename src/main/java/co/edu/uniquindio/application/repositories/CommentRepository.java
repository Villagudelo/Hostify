package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByBookingId(Long bookingId);
}
