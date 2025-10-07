package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.exceptions.ValidationException;

public interface PasswordResetService {

    /**
     * Generar y enviar código de recuperación
     */
    void generateAndSendResetCode(String email) throws ValidationException;

    /**
     * Validar código y restablecer contraseña
     */
    void validateCodeAndResetPassword(String email, String code, String newPassword) throws ValidationException;
}