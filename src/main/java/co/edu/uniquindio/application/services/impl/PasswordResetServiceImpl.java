package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.model.entity.PasswordResetCode;
import co.edu.uniquindio.application.model.entity.User;
import co.edu.uniquindio.application.repositories.PasswordResetCodeRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.EmailService;
import co.edu.uniquindio.application.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetCodeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_MINUTES = 15;

    @Override
    @Transactional
    public void generateAndSendResetCode(String email) throws ValidationException {
        // Verificar que el usuario existe
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ValidationException("No existe un usuario con este email");
        }

        // Invalidar códigos anteriores
        resetCodeRepository.invalidatePreviousCodes(email);

        // Generar nuevo código
        String code = generateRandomCode();
        LocalDateTime now = LocalDateTime.now();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .email(email)
                .code(code)
                .createdAt(now)
                .expiresAt(now.plusMinutes(CODE_EXPIRATION_MINUTES))
                .used(false)
                .build();

        // Guardar código
        resetCodeRepository.save(resetCode);

        // Enviar email con PDF adjunto
        emailService.sendResetCodeEmail(email, code);

        // Limpiar códigos expirados
        resetCodeRepository.deleteExpiredCodes(now);
    }

    @Override
    @Transactional
    public void validateCodeAndResetPassword(String email, String code, String newPassword) throws ValidationException {
        // Validar nueva contraseña
        if (!securePassword(newPassword)) {
            throw new ValidationException("La nueva contraseña debe tener al menos 8 caracteres, una mayúscula y un número");
        }

        // Buscar código válido
        Optional<PasswordResetCode> resetCodeOpt = resetCodeRepository
                .findByEmailAndCodeAndUsedFalse(email, code);

        if (resetCodeOpt.isEmpty()) {
            throw new ValidationException("Código inválido o ya utilizado");
        }

        PasswordResetCode resetCode = resetCodeOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // Verificar que no haya expirado
        if (now.isAfter(resetCode.getExpiresAt())) {
            throw new ValidationException("El código ha expirado. Por favor solicita uno nuevo.");
        }

        // Buscar usuario
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marcar código como usado
        resetCode.setUsed(true);
        resetCodeRepository.save(resetCode);
    }

    /**
     * Generar código aleatorio de 6 dígitos
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10)); // Dígitos del 0-9
        }

        return code.toString();
    }

    private boolean securePassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password != null && password.matches(passwordRegex);
    }
}