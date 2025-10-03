package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.user.*;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<TokenDTO>> login(@Valid @RequestBody LoginDTO loginDTO) throws Exception{
        TokenDTO token = userService.login(loginDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, token));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateUserDTO userDTO) throws Exception{
        userService.create(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El registro ha sido exitoso"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO<String>> sendVerificationCode(@RequestBody ForgotPasswordDTO forgotPasswordDTO) throws Exception{
        //TODO llamar al servicio para enviar el código
        return ResponseEntity.ok(new ResponseDTO<>(false, "Código enviado"));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ResponseDTO<String>> changePassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws Exception{
        userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Contraseña cambiada"));
    }
}
