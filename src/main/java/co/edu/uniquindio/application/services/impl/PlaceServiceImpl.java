package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.Comment.CommentDTO;
import co.edu.uniquindio.application.dto.place.*;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.model.entity.Place;
import co.edu.uniquindio.application.model.entity.User;
import co.edu.uniquindio.application.model.enums.BookingStatus;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.ImageService;
import co.edu.uniquindio.application.services.PlaceService;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.application.model.enums.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    @Override
    public void create(CreatePlaceDTO placeDTO, String hostEmail) throws Exception {
        List<String> finalImages = new ArrayList<>();

        if (placeDTO.imageFiles() != null && !placeDTO.imageFiles().isEmpty()) {
            if (placeDTO.imageFiles().size() > 10) {
                throw new ValidationException("Máximo 10 imágenes permitidas");
            }
            List<String> uploadedUrls = imageService.uploadImages(placeDTO.imageFiles());
            finalImages.addAll(uploadedUrls);
        }

        if (placeDTO.imageUrls() != null && !placeDTO.imageUrls().isEmpty()) {
            if (placeDTO.imageUrls().size() > 10) {
                throw new ValidationException("Máximo 10 imágenes permitidas");
            }
            finalImages.addAll(placeDTO.imageUrls());
        }

        if (finalImages.size() < 1 || finalImages.size() > 10) {
            throw new ValidationException("Debes subir entre 1 y 10 imágenes");
        }
        
    
        User host = userRepository.findByEmail(hostEmail)
            .orElseThrow(() -> new NotFoundException("Anfitrión no encontrado"));

        Place place = new Place();
        place.setTitle(placeDTO.title());
        place.setDescription(placeDTO.description());
        place.setMaxGuests(placeDTO.maxGuests());
        place.setPrice(placeDTO.nightlyPrice());
        place.setImages(finalImages);
        place.setServices(placeDTO.services());
        place.setHost(host);
        place.setStatus(co.edu.uniquindio.application.model.enums.Status.ACTIVE);

        place.setLatitude(placeDTO.latitude());
        place.setLongitude(placeDTO.longitude());
        placeRepository.save(place);
    }

    @Override
    public void edit(Long id, EditPlaceDTO placeDTO, String hostEmail) throws Exception {
        Place place = placeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Alojamiento no encontrado"));

        // Validar que el usuario autenticado es el anfitrión
        if (!place.getHost().getEmail().equals(hostEmail)) {
            throw new co.edu.uniquindio.application.exceptions.ValidationException(
                "Solo el anfitrión puede editar este alojamiento"
            );
        }

        place.setTitle(placeDTO.title());
        place.setDescription(placeDTO.description());
        place.setMaxGuests(placeDTO.maxGuests());
        place.setPrice(placeDTO.nightlyPrice());
        place.setImages(placeDTO.images());
        place.setServices(placeDTO.services());

        place.setLatitude(placeDTO.latitude());
        place.setLongitude(placeDTO.longitude());
        placeRepository.save(place);
    }

    @Override
    public void delete(Long id, String hostEmail) throws Exception {
        Place place = placeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Alojamiento no encontrado"));

        // Validar que el usuario autenticado es el anfitrión
        if (!place.getHost().getEmail().equals(hostEmail)) {
            throw new co.edu.uniquindio.application.exceptions.ValidationException(
                "Solo el anfitrión puede eliminar este alojamiento"
            );
        }

        // Validar que no existan reservas futuras (CONFIRMED o PENDING)
        int futureBookings = bookingRepository.countFutureBookings(id, LocalDateTime.now());
        if (futureBookings > 0) {
            throw new co.edu.uniquindio.application.exceptions.ValidationException(
                "No puedes eliminar el alojamiento porque tiene reservas futuras"
            );
        }

        // Soft delete: cambiar estado a ELIMINATED
        place.setStatus(co.edu.uniquindio.application.model.enums.Status.ELIMINATED);
        placeRepository.save(place);
    }

    @Override
    public PlaceDTO getById(Long id) throws Exception {
        return null;
    }

    @Override
    public MetricsDTO getMetricsById(Long placeId, LocalDateTime from, LocalDateTime to) throws Exception {
        int totalBookings = bookingRepository.countBookingsByPlaceAndDates(placeId, from, to);
        Double avgRating = commentRepository.averageRatingByPlaceAndDates(placeId, from, to);
        int totalReviews = commentRepository.countReviewsByPlaceAndDates(placeId, from, to);

        if (avgRating == null) avgRating = 0.0;

        return new MetricsDTO(totalReviews, avgRating, totalBookings);
    }

    @Override
    public List<ItemPlaceDTO> getPlacesUser(String id) throws Exception {
        return List.of();
    }

    @Override
    public List<ItemPlaceDTO> searchPlaces(SearchPlaceDTO searchDTO) throws Exception {
        List<Service> serviceEnums = null;
        if (searchDTO.services() != null && !searchDTO.services().isEmpty()) {
            serviceEnums = searchDTO.services().stream()
                .map(s -> Service.valueOf(s.trim().toUpperCase()))
                .collect(Collectors.toList());
        }

        List<Place> places = placeRepository.searchAvailablePlaces(
            searchDTO.city(),
            searchDTO.checkIn(),
            searchDTO.checkOut(),
            searchDTO.minPrice(),
            searchDTO.maxPrice(),
            serviceEnums,
            BookingStatus.CONFIRMED 
        );

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
                c.getCreatedAt(),
                c.getHostReply()
            )).toList();

        List<AvailabilityDTO> availability = bookingRepository.findByPlaceIdAndStatus(
            placeId, BookingStatus.CONFIRMED
        ).stream()
            .map(b -> new AvailabilityDTO(b.getCheckIn(), b.getCheckOut()))
            .toList();

        Double averageRating = commentRepository.averageRatingByPlaceAndDates(
        placeId,
        LocalDateTime.MIN,
        LocalDateTime.MAX
    );
    if (averageRating == null) averageRating = 0.0;
    
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
            place.getServices(),
            comments,
            availability,
            averageRating
        );
    }
}
