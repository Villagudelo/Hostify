package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.place.*;

import java.time.LocalDateTime;
import java.util.List;

public interface PlaceService {

    void create(CreatePlaceDTO placeDTO) throws Exception;

    void edit(Long id, EditPlaceDTO placeDTO) throws Exception;

    void delete(Long id) throws Exception;

    PlaceDTO getById(Long id) throws Exception;

    MetricsDTO getMetricsById(Long placeId, LocalDateTime from, LocalDateTime to) throws Exception;

    List<ItemPlaceDTO> getPlacesUser(String id) throws Exception;

    List<ItemPlaceDTO> searchPlaces(SearchPlaceDTO searchDTO) throws Exception;

    PlaceDetailDTO getPlaceDetail(Long placeId) throws Exception;
}
