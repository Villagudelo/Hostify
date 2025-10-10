package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.user.*;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:app-test.properties")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private CreateUserDTO validUserDTO;
    private LoginDTO validLoginDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba con un email único usando timestamp
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";

        validUserDTO = new CreateUserDTO(
            "Usuario Prueba",
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
        // Primero registramos un usuario usando el servicio directamente
        userService.create(validUserDTO);

        // Intentamos hacer login
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").exists());
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
                .andExpect(status().is4xxClientError())
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void forgotPasswordTest() throws Exception {
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("test@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value("Código enviado"));
    }
}
