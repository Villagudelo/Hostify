package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.place.*;
import co.edu.uniquindio.application.model.entity.Place;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.services.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public void create(CreatePlaceDTO placeDTO) throws Exception {

    }

    @Override
    public void edit(Long id, EditPlaceDTO placeDTO) throws Exception {

    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public PlaceDTO getById(Long id) throws Exception {
        return null;
    }

    @Override
    public MetricsDTO getMetricsById(Long id) throws Exception {
        return null;
    }

    @Override
    public List<ItemPlaceDTO> getPlacesUser(String id) throws Exception {
        return List.of();
    }

    @Override
    public List<ItemPlaceDTO> searchPlaces(SearchPlaceDTO searchDTO) throws Exception {
        List<Place> places = placeRepository.searchAvailablePlaces(
            searchDTO.city(),
            searchDTO.checkIn(),
            searchDTO.checkOut(),
            searchDTO.minPrice(),
            searchDTO.maxPrice(),
            searchDTO.services()
        );

        // Paginación simple
        int page = searchDTO.page();
        int pageSize = 10;
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, places.size());

        return places.subList(fromIndex, toIndex)
        .stream()
        .map(place -> new ItemPlaceDTO(
            place.getId(),
            place.getTitle(),
            place.getCity(),
            place.getPrice(),
            place.getRating() != null ? place.getRating().floatValue() : 0f,
            place.getMainImage()
        ))
        .collect(Collectors.toList());
    }

}
