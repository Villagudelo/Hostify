package co.edu.uniquindio.application.controllers;


import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.Comment.*;
import co.edu.uniquindio.application.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<CommentDTO>> createComment(
            @RequestBody CreateCommentDTO dto,
            Principal principal) throws Exception {
        String email = principal.getName();
        CommentDTO comment = commentService.createComment(dto, email);
        return ResponseEntity.ok(new ResponseDTO<>(false, comment));
    }

    @PatchMapping("/reply")
    public ResponseEntity<ResponseDTO<String>> replyToComment(
            @RequestBody ReplyCommentDTO dto,
            Principal principal) throws Exception {
        String email = principal.getName();
        commentService.replyToComment(dto, email);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Respuesta enviada correctamente"));
    }

    @GetMapping("/place/{placeId}")
    public ResponseDTO<List<CommentDTO>> getCommentsByPlace(@PathVariable Long placeId) throws Exception {
        List<CommentDTO> comments = commentService.listCommentsByPlace(placeId);
        return new ResponseDTO<>(false, comments);
    }

    @GetMapping("/place/{placeId}/average-rating")
    public ResponseDTO<Double> getAverageRating(@PathVariable Long placeId) throws Exception {
        Double avg = commentService.getAverageRatingByPlace(placeId);
        return new ResponseDTO<>(false, avg);
    }
    
}
