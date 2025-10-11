package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.config.jwt.JwtUtil;
import co.edu.uniquindio.application.dto.Comment.CommentDTO;
import co.edu.uniquindio.application.dto.Comment.CreateCommentDTO;
import co.edu.uniquindio.application.dto.Comment.ReplyCommentDTO;
import co.edu.uniquindio.application.dto.place.CreatePlaceDTO;
import co.edu.uniquindio.application.dto.user.CreateUserDTO;
import co.edu.uniquindio.application.model.enums.BookingStatus;
import co.edu.uniquindio.application.model.enums.Service;
import co.edu.uniquindio.application.services.BookingService;
import co.edu.uniquindio.application.services.CommentService;
import co.edu.uniquindio.application.services.PlaceService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:app-test.properties")
@Transactional
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtUtil jwtUtil;

    private CreateUserDTO guestUserDTO;
    private CreateUserDTO hostUserDTO;
    private String guestEmail;
    private String hostEmail;
    private String guestToken;
    private String hostToken;
    private Long testPlaceId;
    private Long testBookingId;

    @BeforeEach
    void setUp() throws Exception {
        // Configurar usuarios de prueba
        guestEmail = "guest" + System.currentTimeMillis() + "@example.com";
        hostEmail = "host" + System.currentTimeMillis() + "@example.com";

        // Crear usuario guest
        guestUserDTO = new CreateUserDTO(
                "Guest Test User",
                "3001234567",
                guestEmail,
                "Password123*",
                null,
                LocalDate.of(1990, 3, 10)
        );

        // Crear usuario host
        hostUserDTO = new CreateUserDTO(
                "Host Test User",
                "3007654321",
                hostEmail,
                "Password123*",
                null,
                LocalDate.of(1985, 5, 15)
        );

        try {
            userService.create(guestUserDTO);
            userService.create(hostUserDTO);

            // Crear un place de prueba
            CreatePlaceDTO placeDTO = new CreatePlaceDTO(
                    "Test Place for Comments",
                    "Description for test place",
                    4,
                    100000.0f,
                    List.of("https://example.com/image1.jpg"),
                    null,
                    List.of(Service.WIFI),
                    4.7110,
                    -74.0721
            );

            placeService.create(placeDTO, hostEmail);

            // Obtener el ID del place creado (asumiendo que es el primero)
            var places = placeService.getPlacesUser(hostEmail);
            if (!places.isEmpty()) {
                testPlaceId = places.get(0).id();
            }

            // Crear una reserva de prueba COMPLETED para poder comentar
            // Esto depende de tu implementación de BookingService
            // testBookingId = crearReservaDePrueba();

        } catch (Exception e) {
            System.out.println("Error en setup: " + e.getMessage());
        }

        guestToken = jwtUtil.generateToken("1", guestEmail);
        hostToken = jwtUtil.generateToken("2", hostEmail);
    }

    // ========================== TESTS DE CREACIÓN DE COMENTARIOS ==========================

    @Test
    void createCommentWithoutAuthenticationTest() throws Exception {
        String commentJson = """
            {
                "bookingId": 1,
                "text": "Comentario sin autenticación",
                "rating": 4
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCommentWithEmptyTextTest() throws Exception {
        String emptyTextJson = """
            {
                "bookingId": 1,
                "text": "",
                "rating": 5
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/create")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyTextJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void createCommentWithNullTextTest() throws Exception {
        String nullTextJson = """
            {
                "bookingId": 1,
                "text": null,
                "rating": 5
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/create")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullTextJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void createCommentWithInvalidRatingHighTest() throws Exception {
        String invalidRatingJson = """
            {
                "bookingId": 1,
                "text": "Comentario con rating inválido",
                "rating": 6
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/create")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRatingJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void createCommentWithInvalidRatingLowTest() throws Exception {
        String invalidRatingJson = """
            {
                "bookingId": 1,
                "text": "Comentario con rating inválido", 
                "rating": 0
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/create")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRatingJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void createCommentWithNonExistentBookingTest() throws Exception {
        String validCommentJson = """
            {
                "bookingId": 99999,
                "text": "Excelente lugar, muy recomendado",
                "rating": 5
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/create")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCommentJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.content").value("Reserva no encontrada"));
    }

    // ========================== TESTS DE RESPUESTAS A COMENTARIOS ==========================

    @Test
    void replyToCommentWithoutAuthenticationTest() throws Exception {
        String replyJson = """
            {
                "commentId": 1,
                "reply": "Respuesta sin autenticación"
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/comments/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(replyJson))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    //NO PASA
    @Test
    void replyToCommentAsNonHostTest() throws Exception {
        // Primero necesitamos crear un comentario de prueba
        // Esto requiere una reserva completada, así que lo simulamos
        Long existingCommentId = crearComentarioDePrueba();

        String replyJson = """
            {
                "commentId": %d,
                "reply": "Intento de respuesta como guest"
            }
            """.formatted(existingCommentId);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/comments/reply")
                        .header("Authorization", "Bearer " + guestToken) // Guest intentando responder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(replyJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.content").value("Solo el anfitrión puede responder este comentario"));
    }

    @Test
    void replyToNonExistentCommentTest() throws Exception {
        String replyJson = """
            {
                "commentId": 99999,
                "reply": "Respuesta a comentario inexistente"
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/comments/reply")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(replyJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.content").value("Comentario no encontrado"));
    }

    //NO PASA
    @Test
    void replyToCommentSuccessfulTest() throws Exception {
        // Primero crear un comentario de prueba
        Long existingCommentId = crearComentarioDePrueba();

        String replyJson = """
            {
                "commentId": %d,
                "reply": "Gracias por tu comentario, nos alegra que hayas disfrutado tu estancia"
            }
            """.formatted(existingCommentId);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/comments/reply")
                        .header("Authorization", "Bearer " + hostToken) // Host respondiendo
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(replyJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false));
    }

    // ========================== TESTS DE CONSULTA DE COMENTARIOS ==========================

    @Test
    void getCommentsByPlaceWithoutAuthenticationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}", testPlaceId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //NO PASA
    @Test
    void getCommentsByPlaceSuccessfulTest() throws Exception {
        // Crear algunos comentarios primero
        crearComentarioDePrueba();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}", testPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray());
    }

    //NO PASA
    @Test
    void getCommentsByNonExistentPlaceTest() throws Exception {
        Long nonExistentPlaceId = 99999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}", nonExistentPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk()) // Debería devolver array vacío, no error
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    //NO PASA
    @Test
    void getCommentsByPlaceWithCommentsTest() throws Exception {
        // Crear comentarios de prueba
        Long commentId1 = crearComentarioDePrueba();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}", testPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(commentId1))
                .andExpect(jsonPath("$.content[0].authorName").value("Guest Test User"))
                .andExpect(jsonPath("$.content[0].rating").value(5))
                .andExpect(jsonPath("$.content[0].text").value("Excelente lugar para pruebas"));
    }

    // ========================== TESTS DE RATING PROMEDIO ==========================

    @Test
    void getAverageRatingWithoutAuthenticationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/average-rating/{placeId}", testPlaceId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //NO PASA
    @Test
    void getAverageRatingSuccessfulTest() throws Exception {
        // Crear comentarios con diferentes ratings
        crearComentarioConRating(5);
        crearComentarioConRating(3);
        crearComentarioConRating(4);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/average-rating/{placeId}", testPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value(4.0)); // (5+3+4)/3 = 4.0
    }

    //NO PASA
    @Test
    void getAverageRatingForNonExistentPlaceTest() throws Exception {
        Long nonExistentPlaceId = 99999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/average-rating/{placeId}", nonExistentPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value(0.0)); // Rating promedio 0 para lugar sin comentarios
    }

    //NO PASA
    @Test
    void getAverageRatingForPlaceWithNoCommentsTest() throws Exception {
        // Crear un nuevo place sin comentarios
        CreatePlaceDTO newPlaceDTO = new CreatePlaceDTO(
                "Place Sin Comentarios",
                "Descripción",
                2,
                80000.0f,
                List.of("https://example.com/image.jpg"),
                null,
                List.of(Service.WIFI),
                4.7110,
                -74.0721
        );

        placeService.create(newPlaceDTO, hostEmail);

        // Obtener el ID del nuevo place
        var places = placeService.getPlacesUser(hostEmail);
        Long newPlaceId = places.stream()
                .filter(p -> p.title().equals("Place Sin Comentarios"))
                .findFirst()
                .map(p -> p.id())
                .orElse(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/average-rating/{placeId}", newPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value(0.0));
    }

    // ========================== MÉTODOS AUXILIARES ==========================

    /**
     * Método auxiliar para crear un comentario de prueba
     * En un entorno real, esto requeriría una reserva completada
     */
    private Long crearComentarioDePrueba() throws Exception {
        // En un entorno de prueba real, necesitarías:
        // 1. Una reserva completada
        // 2. Usar el commentService para crear el comentario

        // Por ahora, simulamos que existe un comentario
        // En la práctica, necesitarías configurar datos de prueba en la BD
        return 1L;
    }

    private Long crearComentarioConRating(int rating) throws Exception {
        // Similar al método anterior pero con rating específico
        // En la práctica, necesitarías crear comentarios reales en la BD
        return (long) rating;
    }
}