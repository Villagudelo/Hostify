package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.place.*;
import co.edu.uniquindio.application.services.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
}
