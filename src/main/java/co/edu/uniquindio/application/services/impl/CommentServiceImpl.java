package co.edu.uniquindio.application.services.impl;


import co.edu.uniquindio.application.dto.Comment.*;
import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.model.entity.*;
import co.edu.uniquindio.application.model.enums.*;
import co.edu.uniquindio.application.repositories.*;
import co.edu.uniquindio.application.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Override
    public CommentDTO createComment(CreateCommentDTO dto, String email) throws Exception {
        Booking booking = bookingRepository.findById(dto.bookingId())
                .orElseThrow(() -> new ValidationException("Reserva no encontrada"));

        // Validar que el usuario es el dueño de la reserva
        if (!booking.getGuest().getEmail().equals(email)) {
            throw new ValidationException("Solo puedes comentar tus reservas");
        }

        // Validar que la reserva esté completada
        if (!booking.getStatus().equals(BookingStatus.COMPLETED)) {
            throw new ValidationException("Solo puedes comentar reservas completadas");
        }

        // Validar que no exista ya un comentario para esa reserva
        if (commentRepository.existsByBookingId(dto.bookingId())) {
            throw new ValidationException("Solo puedes dejar un comentario por reserva");
        }

        Comment comment = Comment.builder()
                .author(booking.getGuest())
                .place(booking.getPlace())
                .booking(booking)
                .rating(dto.rating())
                .text(dto.text())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return new CommentDTO(
                comment.getId(),
                comment.getAuthor().getName(),
                comment.getRating(),
                comment.getText(),
                comment.getCreatedAt()
        );
    }
}
