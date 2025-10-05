package co.edu.uniquindio.application.dto.Comment;

import jakarta.validation.constraints.NotBlank;

public record ReplyCommentDTO(
    Long commentId,
    @NotBlank String reply
) {}
