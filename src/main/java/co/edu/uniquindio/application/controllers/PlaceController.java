package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.place.*;
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
}
