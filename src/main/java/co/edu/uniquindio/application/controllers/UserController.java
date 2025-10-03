package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.user.*;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

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

}