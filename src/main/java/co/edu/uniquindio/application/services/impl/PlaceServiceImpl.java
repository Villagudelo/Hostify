package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.place.*;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.services.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
