package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.config.jwt.JwtUtil;
import co.edu.uniquindio.application.dto.Comment.CommentDTO;
import co.edu.uniquindio.application.dto.Comment.CreateCommentDTO;
import co.edu.uniquindio.application.dto.Comment.ReplyCommentDTO;
import co.edu.uniquindio.application.dto.booking.CreateBookingDTO;
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

            // ✅ CREAR PLACE CON TODOS LOS CAMPOS REQUERIDOS
            CreatePlaceDTO placeDTO = new CreatePlaceDTO(
                    "Test Place for Comments",       // title
                    "Description for test place",    // description  
                    4,                              // maxGuests
                    100000.0f,                      // nightlyPrice
                    List.of("https://example.com/image1.jpg"), // imageUrls
                    null,                           // imageFiles
                    List.of(Service.WIFI),          // services
                    4.7110,                         // latitude
                    -74.0721,  
                    "Bogota",
                    "Km 5"               
            );

            // ✅ CAPTURAR Y ASIGNAR EL ID DEL PLACE
            Long placeId = placeService.create(placeDTO, hostEmail);
            testPlaceId = placeId;

            System.out.println("✅ Test Place ID asignado: " + testPlaceId);

        } catch (Exception e) {
            System.out.println("❌ Error en setup: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-lanzar para que falle el test
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
                "rating": 4,
                "text": "Comentario sin autenticación"
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
                "rating": 5,
                "text": ""
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
                "rating": 5,
                "text": null
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
                "rating": 6,
                "text": "Comentario con rating inválido"
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
                "rating": 0,
                "text": "Comentario con rating inválido"
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
                "rating": 5,
                "text": "Excelente lugar, muy recomendado"
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

    @Test
    void createCommentSuccessfulTest() throws Exception {
        // ✅ CREAR RESERVA Y COMENTARIO REAL
        Long commentId = crearComentarioDePrueba();
        
        // Verificar que se creó correctamente
        assert commentId != null && commentId > 0;
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

    @Test
    void replyToCommentAsNonHostTest() throws Exception {
        // ✅ CREAR COMENTARIO REAL
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

    @Test
    void replyToCommentSuccessfulTest() throws Exception {
        // ✅ CREAR COMENTARIO REAL
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

    @Test
    void getCommentsByPlaceSuccessfulTest() throws Exception {
        // ✅ CREAR COMENTARIO REAL
        crearComentarioDePrueba();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}", testPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getCommentsByNonExistentPlaceTest() throws Exception {
        Long nonExistentPlaceId = 99999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}", nonExistentPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void getCommentsByPlaceWithCommentsTest() throws Exception {
        // ✅ CREAR COMENTARIO REAL
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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}/average-rating", testPlaceId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAverageRatingSuccessfulTest() throws Exception {
        // ✅ CREAR COMENTARIOS REALES CON DIFERENTES RATINGS
        crearComentarioConRating(5);
        crearComentarioConRating(3);
        crearComentarioConRating(4);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}/average-rating", testPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value(4.0)); // (5+3+4)/3 = 4.0
    }

    @Test
    void getAverageRatingForNonExistentPlaceTest() throws Exception {
        Long nonExistentPlaceId = 99999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}/average-rating", nonExistentPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value(0.0));
    }

    @Test
    void getAverageRatingForPlaceWithNoCommentsTest() throws Exception {
        // ✅ CREAR NUEVO PLACE
        CreatePlaceDTO newPlaceDTO = new CreatePlaceDTO(
                "Place Sin Comentarios",
                "Descripción lugar sin comentarios",
                2,
                80000.0f,
                List.of("https://example.com/image.jpg"),
                null,
                List.of(Service.WIFI),
                4.7110,
                -74.0721,
                "bogota",
                "km 5"
        );

        Long newPlaceId = placeService.create(newPlaceDTO, hostEmail);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/place/{placeId}/average-rating", newPlaceId)
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value(0.0));
    }

    // ========================== MÉTODOS AUXILIARES ==========================

    /**
     * ✅ MÉTODO AUXILIAR REAL PARA CREAR COMENTARIO DE PRUEBA
     */
    private Long crearComentarioDePrueba() throws Exception {
        if (testPlaceId == null) {
            throw new IllegalStateException("testPlaceId no puede ser null");
        }

        System.out.println("🔧 Creando reserva para place ID: " + testPlaceId);

        // ✅ CREAR RESERVA REAL
        CreateBookingDTO bookingDTO = new CreateBookingDTO(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(3),
            testPlaceId,
            2
        );
        
        Long bookingId = bookingService.create(bookingDTO, guestEmail);
        System.out.println("✅ Reserva creada con ID: " + bookingId);
        
        // ✅ FLUJO CORRECTO: PENDING → CONFIRMED → COMPLETED
        bookingService.updateStatus(bookingId, BookingStatus.CONFIRMED, hostEmail);
        System.out.println("✅ Reserva marcada como CONFIRMED");
        
        bookingService.updateStatus(bookingId, BookingStatus.COMPLETED, hostEmail);
        System.out.println("✅ Reserva marcada como COMPLETED");
        
        // ✅ CREAR COMENTARIO REAL
        CreateCommentDTO commentDTO = new CreateCommentDTO(
            bookingId,
            5,
            "Excelente lugar para pruebas"
        );
        
        CommentDTO createdComment = commentService.createComment(commentDTO, guestEmail);
        System.out.println("✅ Comentario creado con ID: " + createdComment.id());
        
        return createdComment.id();
    }

    /**
     * ✅ CREAR COMENTARIO CON RATING ESPECÍFICO
     */
    private Long crearComentarioConRating(int rating) throws Exception {
        if (testPlaceId == null) {
            throw new IllegalStateException("testPlaceId no puede ser null");
        }

        CreateBookingDTO bookingDTO = new CreateBookingDTO(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(3),
            testPlaceId,
            2
        );
        
        Long bookingId = bookingService.create(bookingDTO, guestEmail);
        
        // ✅ FLUJO CORRECTO: PENDING → CONFIRMED → COMPLETED
        bookingService.updateStatus(bookingId, BookingStatus.CONFIRMED, hostEmail);
        bookingService.updateStatus(bookingId, BookingStatus.COMPLETED, hostEmail);
        
        CreateCommentDTO commentDTO = new CreateCommentDTO(
            bookingId,
            rating,
            "Comentario con rating " + rating
        );
        
        CommentDTO createdComment = commentService.createComment(commentDTO, guestEmail);
        return createdComment.id();
    }
}