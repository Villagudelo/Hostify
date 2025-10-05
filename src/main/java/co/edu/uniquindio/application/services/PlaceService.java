package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.place.*;
import java.util.List;

public interface PlaceService {

    void create(CreatePlaceDTO placeDTO) throws Exception;

    void edit(Long id, EditPlaceDTO placeDTO) throws Exception;

    void delete(Long id) throws Exception;

    PlaceDTO getById(Long id) throws Exception;

    MetricsDTO getMetricsById(Long id) throws Exception;

    List<ItemPlaceDTO> getPlacesUser(String id) throws Exception;

    List<ItemPlaceDTO> searchPlaces(SearchPlaceDTO searchDTO) throws Exception;

    PlaceDetailDTO getPlaceDetail(Long placeId) throws Exception;
}
