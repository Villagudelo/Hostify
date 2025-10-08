package co.edu.uniquindio.application.services.impl;


import co.edu.uniquindio.application.dto.Comment.*;
import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.model.entity.*;
import co.edu.uniquindio.application.model.enums.*;
import co.edu.uniquindio.application.repositories.*;
import co.edu.uniquindio.application.services.CommentService;
import co.edu.uniquindio.application.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
     private final EmailService emailService;

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

    // Validar rango de rating
    if (dto.rating() < 1 || dto.rating() > 5) {
        throw new ValidationException("La calificación debe estar entre 1 y 5");
    }

    // Validar texto no vacío
    if (dto.text() == null || dto.text().trim().isEmpty()) {
        throw new ValidationException("El comentario no puede estar vacío");
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

    User host = comment.getPlace().getHost();
        emailService.sendNewCommentNotification(
            host.getEmail(),
            comment.getAuthor().getName(),
            comment.getPlace().getTitle(),
            comment.getText(),
            comment.getRating()
        );

    return new CommentDTO(
            comment.getId(),
            comment.getAuthor().getName(),
            comment.getRating(),
            comment.getText(),
            comment.getCreatedAt(),
            comment.getHostReply() // <-- Agrega la respuesta del anfitrión si existe
    );
}

    @Override
    public void replyToComment(ReplyCommentDTO dto, String email) throws Exception {
        Comment comment = commentRepository.findById(dto.commentId())
            .orElseThrow(() -> new ValidationException("Comentario no encontrado"));

        // Validar que el usuario autenticado es el anfitrión del alojamiento
        User host = comment.getPlace().getHost();
        if (!host.getEmail().equals(email)) {
            throw new ValidationException("Solo el anfitrión puede responder este comentario");
        }

        comment.setHostReply(dto.reply());
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDTO> listCommentsByPlace(Long placeId) throws Exception {
        List<Comment> comments = commentRepository.findByPlaceIdOrderByCreatedAtDesc(placeId);
        return comments.stream()
            .map(comment -> new CommentDTO(
                comment.getId(),
                comment.getAuthor().getName(),
                comment.getRating(),
                comment.getText(),
                comment.getCreatedAt(),
                comment.getHostReply()
            ))
            .toList();
    }

    @Override
    public Double getAverageRatingByPlace(Long placeId) throws Exception {
        Double avg = commentRepository.findAverageRatingByPlaceId(placeId);
        // Si no hay comentarios, retorna 0.0
        return avg != null ? avg : 0.0;
    }
}
