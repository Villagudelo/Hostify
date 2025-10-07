package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.user.*;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.entity.User;
import co.edu.uniquindio.application.model.enums.Status;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.PasswordResetService;
import co.edu.uniquindio.application.services.UserService;
import co.edu.uniquindio.application.services.impl.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordResetService passwordResetService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> get(@PathVariable String id) throws Exception{
        UserDTO userDTO = userService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception{
        userService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido eliminado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditUserDTO userDTO) throws Exception{
        userService.edit(id, userDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido actualizado"));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ResponseDTO<String>> changePassword(@PathVariable String id, @Valid @RequestBody ChangePasswordDTO changePasswordDTO) throws Exception{
        userService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La contraseña ha sido cambiada"));
    }

    // Endpoint para subir foto de perfil
    @PostMapping("/{id}/photo")
    public ResponseEntity<ResponseDTO<String>> uploadPhoto(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) throws Exception {

        String photoUrl = fileStorageService.storeFile(file, id);
        userService.updatePhoto(id, photoUrl);

        return ResponseEntity.ok(new ResponseDTO<>(false, "Foto de perfil actualizada"));
    }

    //Temporal para prueba
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();

            // Mapeo manual para evitar problemas
            List<UserDTO> userDTOs = users.stream()
                    .filter(user -> user.getStatus() == Status.ACTIVE)
                    .map(user -> new UserDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getPhotoUrl(),
                            user.getDateBirth(),
                            user.getCreatedAt(),
                            user.getRole().name(),
                            user.getStatus().name()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ResponseDTO<>(false, userDTOs));

        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO<>(true, List.of()));
        }
    }

    // Endpoint para solicitar código de recuperación
    @PostMapping("/password-reset/request")
    public ResponseEntity<ResponseDTO<String>> requestPasswordReset(@RequestBody PasswordResetRequestDTO requestDTO) {
        try {
            passwordResetService.generateAndSendResetCode(requestDTO.email());
            return ResponseEntity.ok(new ResponseDTO<>(false, "Código de recuperación enviado a tu email"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO<>(true, e.getMessage()));
        }
    }

    // Endpoint para resetear contraseña con código
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<ResponseDTO<String>> confirmPasswordReset(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            userService.resetPassword(resetPasswordDTO);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Contraseña restablecida exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO<>(true, e.getMessage()));
        }
    }
}