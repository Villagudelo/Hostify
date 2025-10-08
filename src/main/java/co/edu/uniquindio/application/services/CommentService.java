package co.edu.uniquindio.application.services;

import java.util.List;

import co.edu.uniquindio.application.dto.Comment.*;;


public interface CommentService {
    CommentDTO createComment(CreateCommentDTO dto, String email) throws Exception;

    void replyToComment(ReplyCommentDTO dto, String email) throws Exception;

    List<CommentDTO> listCommentsByPlace(Long placeId) throws Exception;

    Double getAverageRatingByPlace(Long placeId) throws Exception;

}