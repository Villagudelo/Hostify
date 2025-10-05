package co.edu.uniquindio.application.dto.place;

import java.time.LocalDate;
import java.util.List;

public record SearchPlaceDTO(
    String city,
    LocalDate checkIn,
    LocalDate checkOut,
    Double minPrice,
    Double maxPrice,
    List<String> services,
    int page
) {}
