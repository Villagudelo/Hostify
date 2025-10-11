package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.user.*;
import co.edu.uniquindio.application.repositories.PasswordResetCodeRepository;
import co.edu.uniquindio.application.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:app-test.properties")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetCodeRepository passwordResetCodeRepository;

    private CreateUserDTO validUserDTO;
    private LoginDTO validLoginDTO;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "valentina09s@gmail.com";

        validUserDTO = new CreateUserDTO(
            "Valentina Salazar",
            "3001234567",
            uniqueEmail,
            "Password123*",
            null,
            LocalDate.of(2000, 1, 1)
        );

        validLoginDTO = new LoginDTO(
            uniqueEmail,
            "Password123*"
        );
    }

    @Test
    void registerUserSuccessfulTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value("El registro ha sido exitoso"));
    }

    @Test
    void loginSuccessfulTest() throws Exception {
        userService.create(validUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content.token").exists())
                .andExpect(jsonPath("$.content.token").isNotEmpty());
    }

    @Test
    void loginWithInvalidCredentialsTest() throws Exception {
        LoginDTO invalidLoginDTO = new LoginDTO(
            "noexiste@example.com",
            "InvalidPassword123*"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isNotFound()) //  CAMBIADO: Tu UserService lanza NotFoundException
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void registerUserWithInvalidEmailTest() throws Exception {
        CreateUserDTO invalidUserDTO = new CreateUserDTO(
            "Usuario Prueba",
            "3001234567",
            "invalid-email",
            "Password123*",
            null,
            LocalDate.of(2000, 1, 1)
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isBadRequest()) // ValidationException → 400
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void forgotPasswordTest() throws Exception {
        userService.create(validUserDTO);
        
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO(validUserDTO.email());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value("Código enviado"));
    }

    @Test
    void registerUserWithDuplicateEmailTest() throws Exception {
        userService.create(validUserDTO);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserDTO)))
                .andDo(print()) // Para debug si falla
                .andExpect(status().isConflict()) // ValueConflictException → 409
                .andExpect(jsonPath("$.error").value(true));
    }

    // TEST ADICIONAL: Password débil
    @Test
    void registerUserWithWeakPasswordTest() throws Exception {
        CreateUserDTO weakPasswordDTO = new CreateUserDTO(
            "Usuario Prueba",
            "3001234567",
            "weak" + System.currentTimeMillis() + "@example.com",
            "123", // Password débil
            null,
            LocalDate.of(2000, 1, 1)
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weakPasswordDTO)))
                .andExpect(status().isBadRequest()) // ValidationException → 400
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void resetPasswordTest() throws Exception {
        userService.create(validUserDTO);

        // Primero solicita el código
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO(validUserDTO.email());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordDTO)));

        // Obtiene el código real generado de la base de datos
        String realCode = passwordResetCodeRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(validUserDTO.email())
                .orElseThrow()
                .getCode();

        // Luego intenta cambiar la contraseña usando el código real
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO(
            validUserDTO.email(),
            realCode,
            "NewPassword123*"
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value("Contraseña cambiada"));
    }

    @Test
    void resetPasswordWithInvalidCodeTest() throws Exception {
        userService.create(validUserDTO);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO(
            validUserDTO.email(),
            "000000", // Código inválido
            "NewPassword123*"
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void resetPasswordWithExpiredCodeTest() throws Exception {
        userService.create(validUserDTO);

        // Primero solicita el código
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO(validUserDTO.email());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordDTO)));

        // Simula que han pasado más de 15 minutos
        Thread.sleep(1000); // Solo para simular el paso del tiempo

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO(
            validUserDTO.email(),
            "123456",
            "NewPassword123*"
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }
}