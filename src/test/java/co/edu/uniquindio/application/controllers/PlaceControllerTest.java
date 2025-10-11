package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.config.jwt.JwtUtil;
import co.edu.uniquindio.application.dto.place.*;
import co.edu.uniquindio.application.dto.user.*;
import co.edu.uniquindio.application.model.enums.Service;
import co.edu.uniquindio.application.services.UserService;
import co.edu.uniquindio.application.services.PlaceService;
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

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:app-test.properties")
@Transactional
public class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private JwtUtil jwtUtil;

    private CreateUserDTO hostUserDTO;
    private String hostEmail;
    private String hostToken;

    @BeforeEach
    void setUp() throws Exception {
        // Limpiar cualquier estado anterior
        hostEmail = "host" + System.currentTimeMillis() + "@example.com";

        // Crear usuario host con email único
        hostUserDTO = new CreateUserDTO(
            "Host Test User",
            "3001234567",
            hostEmail,
            "Password123*",
            null,
            LocalDate.of(1990, 3, 10)
        );

        try {
            userService.create(hostUserDTO);
        } catch (Exception e) {
            // Si el usuario ya existe, continuar
            System.out.println("Usuario ya existe o error al crear: " + e.getMessage());
        }
        
        hostToken = jwtUtil.generateToken("1", hostEmail);
    }

    // ========================== TESTS DE AUTENTICACIÓN (BÁSICOS) ==========================

    @Test
    void createPlaceWithoutAuthenticationTest() throws Exception {
        String placeJson = """
            {
                "title": "Casa Test",
                "description": "Descripción test",
                "maxGuests": 2,
                "nightlyPrice": 100000.0,
                "imageUrls": ["https://example.com/image1.jpg"],
                "services": ["WIFI"],
                "latitude": 10.0,
                "longitude": -75.0
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/places/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(placeJson))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPlaceWithInvalidTokenTest() throws Exception {
        String placeJson = """
            {
                "title": "Casa Test",
                "description": "Descripción test",
                "maxGuests": 2,
                "nightlyPrice": 100000.0,
                "imageUrls": ["https://example.com/image1.jpg"],
                "services": ["WIFI"],
                "latitude": 10.0,
                "longitude": -75.0
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/places/create")
                .header("Authorization", "Bearer invalid-jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(placeJson))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ========================== TESTS DE VALIDACIÓN ==========================

    @Test
    void createPlaceWithNoImagesTest() throws Exception {
        String noImagesJson = """
            {
                "title": "Casa Sin Imágenes",
                "description": "Casa sin imágenes para test",
                "maxGuests": 2,
                "nightlyPrice": 100000.0,
                "imageUrls": [],
                "services": ["WIFI"],
                "latitude": 4.7110,
                "longitude": -74.0721
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/places/create")
                .header("Authorization", "Bearer " + hostToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(noImagesJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void createPlaceWithNullFieldsTest() throws Exception {
        String jsonWithNulls = """
            {
                "title": null,
                "description": null,
                "maxGuests": null,
                "nightlyPrice": null,
                "imageUrls": null,
                "services": null,
                "latitude": null,
                "longitude": null
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/places/create")
                .header("Authorization", "Bearer " + hostToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithNulls))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }

    // ========================== TESTS DE BÚSQUEDA (CORREGIDOS) ==========================

    @Test
    void searchPlacesWithoutAuthenticationTest() throws Exception {
        String searchJson = """
            {
                "city": "Cartagena",
                "checkIn": "2025-10-15",
                "checkOut": "2025-10-18",
                "minPrice": 50000.0,
                "maxPrice": 200000.0,
                "services": ["WIFI", "POOL"],
                "page": 0
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/places/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(searchJson))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchPlacesWithEmptyCriteriaTest() throws Exception {
        String emptySearchJson = """
            {
                "city": null,
                "checkIn": null,
                "checkOut": null,
                "minPrice": null,
                "maxPrice": null,
                "services": null,
                "page": 0
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/places/search")
                .header("Authorization", "Bearer " + hostToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptySearchJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray());
    }

    // ========================== TESTS DE DETALLES ==========================

    @Test
    void getPlaceDetailWithNonExistentIdTest() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/places/detail/{placeId}", nonExistentId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPlaceDetailWithAuthenticationTest() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/places/detail/{placeId}", nonExistentId)
                .header("Authorization", "Bearer " + hostToken))
                .andDo(print())
                .andExpect(status().isNotFound()) // CORREGIDO: 404
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.content").value("Alojamiento no encontrado"));
    }

    // ========================== TESTS DE MÉTRICAS ==========================

    @Test
    void getPlaceMetricsWithNonExistentIdTest() throws Exception {
        Long nonExistentId = 99999L;
        LocalDateTime from = LocalDateTime.now().minusMonths(1);
        LocalDateTime to = LocalDateTime.now();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/places/metrics/{placeId}", nonExistentId)
                .header("Authorization", "Bearer " + hostToken)
                .param("from", from.toString())
                .param("to", to.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false));
    }

    // ========================== TESTS DE ELIMINACIÓN ==========================

    @Test
    void deleteNonExistentPlaceTest() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/places/delete/{id}", nonExistentId)
                .header("Authorization", "Bearer " + hostToken))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void deletePlaceWithoutAuthenticationTest() throws Exception {
        Long placeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/places/delete/{id}", placeId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ========================== TESTS DE EDICIÓN ==========================

    @Test
    void editPlaceWithoutAuthenticationTest() throws Exception {
        Long placeId = 1L;
        
        EditPlaceDTO editDTO = new EditPlaceDTO(
            "Título",
            "Descripción",
            4,
            150000.0f,
            List.of("https://example.com/image.jpg"),
            List.of(Service.WIFI),
            "Dirección 123",
            10.0,
            -75.0
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/places/edit/{id}", placeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ========================== TESTS DE AUTOCOMPLETADO (CORREGIDOS) ==========================

    @Test
    void autocompleteCityWithValidPrefixTest() throws Exception {
        String prefix = "Cart";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/places/autocomplete-city")
                .header("Authorization", "Bearer " + hostToken) //AGREGADO: Autenticación requerida
                .param("prefix", prefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void autocompleteCityWithEmptyPrefixTest() throws Exception {
        String emptyPrefix = "";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/places/autocomplete-city")
                .header("Authorization", "Bearer " + hostToken) //AGREGADO: Autenticación requerida
                .param("prefix", emptyPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray());
    }

    // ========================== TESTS DE MIS PLACES ==========================

    @Test
    void getMyPlacesSuccessfulTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/places/my-places")
                .header("Authorization", "Bearer " + hostToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getMyPlacesWithoutAuthenticationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/places/my-places"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ========================== TESTS BÁSICOS DE FAVORITOS (SIN DB) ==========================

    @Test
    void addFavoriteWithoutAuthenticationTest() throws Exception {
        Long placeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/places/favorite/{placeId}", placeId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeFavoriteWithoutAuthenticationTest() throws Exception {
        Long placeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/places/favorite/{placeId}", placeId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserFavoritesWithoutAuthenticationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/places/favorites"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}