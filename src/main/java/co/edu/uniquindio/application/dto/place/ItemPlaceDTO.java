package co.edu.uniquindio.application.dto.place;

public record ItemPlaceDTO(
        Long id,
        String title,
        String city,
        float nightlyPrice,
        float rating,
        String coverImage
) {
}
