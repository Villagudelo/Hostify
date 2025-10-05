package co.edu.uniquindio.application.dto.Comment;

import java.time.LocalDateTime;

public record CommentDTO(
    Long id,
    String authorName,
    int rating,
    String text,
    LocalDateTime createdAt
) {}
