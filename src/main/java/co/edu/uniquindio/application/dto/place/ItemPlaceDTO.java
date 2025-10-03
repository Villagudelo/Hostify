package co.edu.uniquindio.application.dto.place;

public record ItemPlaceDTO(
        Long id,
        String title,
        float nightlyPrice,
        float rating,
        String coverImage
) {
}
