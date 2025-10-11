package co.edu.uniquindio.application.dto.place;

import java.util.List;
import co.edu.uniquindio.application.model.enums.Service;

public record PlaceDTO(
        String title,
        String description,
        int maxGuests,
        float nightlyPrice,
        List<String> images,
        List<Service> services,
        String hostName,
        Double latitude,
        Double longitude,
        String address,
        String city

) {
}
