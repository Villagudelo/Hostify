package co.edu.uniquindio.application.controllers;


import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.Comment.*;
import co.edu.uniquindio.application.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
}
