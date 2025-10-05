package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.Comment.CommentDTO;
import co.edu.uniquindio.application.dto.place.*;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.model.entity.Place;
import co.edu.uniquindio.application.model.enums.BookingStatus;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.CommentRepository;
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
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

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

    @Override
    public PlaceDetailDTO getPlaceDetail(Long placeId) throws Exception {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new NotFoundException("Alojamiento no encontrado"));

        List<CommentDTO> comments = commentRepository.findByPlaceId(placeId).stream()
            .map(c -> new CommentDTO(
                c.getId(),
                c.getAuthor().getName(),
                c.getRating(),
                c.getText(),
                c.getCreatedAt()
            )).toList();

        List<AvailabilityDTO> availability = bookingRepository.findByPlaceIdAndStatus(
            placeId, BookingStatus.CONFIRMED
        ).stream()
            .map(b -> new AvailabilityDTO(b.getCheckIn(), b.getCheckOut()))
            .toList();

        return new PlaceDetailDTO(
            place.getId(),
            place.getTitle(),
            place.getDescription(),
            place.getCity(),
            place.getAddress(),
            place.getPrice(),
            place.getMaxGuests(),
            place.getImages(),
            place.getLatitude(),
            place.getLongitude(),
            comments,
            availability
        );
    }

}
