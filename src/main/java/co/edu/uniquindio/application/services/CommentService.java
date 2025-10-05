package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.Comment.*;;


public interface CommentService {
    CommentDTO createComment(CreateCommentDTO dto, String email) throws Exception;
}