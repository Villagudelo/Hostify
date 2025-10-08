package co.edu.uniquindio.application.dto.user;


import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserDTO(
        String id,
        String name,
        String email,
        String phone,
        String photoUrl,
        LocalDate dateBirth,
        LocalDateTime createdAt,
        String role,
        String status
) {
}
