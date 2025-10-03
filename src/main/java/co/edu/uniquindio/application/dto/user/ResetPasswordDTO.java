package co.edu.uniquindio.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDTO(
        @Email String email,
        @NotBlank String verificationCode,
        @NotBlank String newPassword
) {
}
