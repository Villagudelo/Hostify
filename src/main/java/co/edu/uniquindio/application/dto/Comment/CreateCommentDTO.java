package co.edu.uniquindio.application.dto.Comment;

import jakarta.validation.constraints.*;

public record CreateCommentDTO(
    Long bookingId,
    @Min(1) @Max(5) int rating,
    @NotBlank @Size(max = 500) String text
) {}
