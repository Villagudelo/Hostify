package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.place.*;
import co.edu.uniquindio.application.services.FavoriteService;
import co.edu.uniquindio.application.services.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final FavoriteService favoriteService;

    @PostMapping("/search")
    public ResponseEntity<ResponseDTO<List<ItemPlaceDTO>>> searchPlaces(@RequestBody SearchPlaceDTO searchDTO) throws Exception {
        List<ItemPlaceDTO> results = placeService.searchPlaces(searchDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, results));
    }

    @GetMapping("/detail/{placeId}")
    public ResponseEntity<ResponseDTO<PlaceDetailDTO>> getPlaceDetail(@PathVariable Long placeId) throws Exception {
        PlaceDetailDTO detail = placeService.getPlaceDetail(placeId);
        return ResponseEntity.ok(new ResponseDTO<>(false, detail));
    }

    @GetMapping("/metrics/{placeId}")
    public ResponseEntity<ResponseDTO<MetricsDTO>> getPlaceMetrics(
            @PathVariable Long placeId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    ) throws Exception {
        MetricsDTO metrics = placeService.getMetricsById(placeId, from, to);
        return ResponseEntity.ok(new ResponseDTO<>(false, metrics));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<String>> createPlace(
            @RequestBody CreatePlaceDTO placeDTO,
            Principal principal) throws Exception {
        String hostEmail = principal.getName();
        placeService.create(placeDTO, hostEmail);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento creado correctamente"));
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO<String>> deletePlace(
            @PathVariable Long id,
            Principal principal) throws Exception {
        String hostEmail = principal.getName();
        placeService.delete(id, hostEmail);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento eliminado correctamente"));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ResponseDTO<String>> editPlace(
            @PathVariable Long id,
            @RequestBody EditPlaceDTO placeDTO,
            Principal principal) throws Exception {
        String hostEmail = principal.getName();
        placeService.edit(id, placeDTO, hostEmail);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento editado correctamente"));
    }

    @GetMapping("/autocomplete-city")
    public ResponseEntity<ResponseDTO<List<String>>> autocompleteCity(@RequestParam String prefix) throws Exception {
        List<String> cities = placeService.autocompleteCities(prefix);
        return ResponseEntity.ok(new ResponseDTO<>(false, cities));
    }

    @PostMapping("/favorite/{placeId}")
    public ResponseEntity<ResponseDTO<String>> addFavorite(@PathVariable Long placeId, Principal principal) throws Exception {
        favoriteService.addFavorite(placeId, principal.getName());
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento marcado como favorito"));
    }

    @DeleteMapping("/favorite/{placeId}")
    public ResponseEntity<ResponseDTO<String>> removeFavorite(@PathVariable Long placeId, Principal principal) throws Exception {
        favoriteService.removeFavorite(placeId, principal.getName());
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento eliminado de favoritos"));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ResponseDTO<List<FavoriteDTO>>> getUserFavorites(Principal principal) throws Exception {
        List<FavoriteDTO> favorites = favoriteService.getUserFavorites(principal.getName());
        return ResponseEntity.ok(new ResponseDTO<>(false, favorites));
    }

    @GetMapping("/favorite-count/{placeId}")
    public ResponseEntity<ResponseDTO<Integer>> getFavoriteCount(@PathVariable Long placeId) throws Exception {
        int count = favoriteService.getFavoriteCount(placeId);
        return ResponseEntity.ok(new ResponseDTO<>(false, count));
    }

    @GetMapping("/my-places")
    public ResponseEntity<ResponseDTO<List<ItemPlaceDTO>>> getMyPlaces(Principal principal) throws Exception {
        String hostEmail = principal.getName();
        List<ItemPlaceDTO> myPlaces = placeService.getPlacesUser(hostEmail);
        return ResponseEntity.ok(new ResponseDTO<>(false, myPlaces));
    }
}
