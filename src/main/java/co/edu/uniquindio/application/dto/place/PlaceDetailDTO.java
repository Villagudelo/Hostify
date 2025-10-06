package co.edu.uniquindio.application.dto.place;

import co.edu.uniquindio.application.dto.Comment.CommentDTO;
import co.edu.uniquindio.application.model.enums.Service;

import java.util.List;

public record PlaceDetailDTO(
    Long id,
    String title,
    String description,
    String city,
    String address,
    float price,
    int maxGuest,
    List<String> images,
    Double latitude,
    Double longitude,
    List<Service> services,
    List<CommentDTO> comments,
    List<AvailabilityDTO> availability,
    Double averageRating
) {}
