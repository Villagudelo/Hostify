package co.edu.uniquindio.application.dto.place;

import co.edu.uniquindio.application.model.enums.Service;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Set;

public record CreatePlaceDTO(
        @NotBlank @Length(max = 150) String title,
        @NotBlank String description,
        @NotNull int maxGuests,
        @NotNull float nightlyPrice,
        @NotEmpty List<String> images,
        @NotEmpty Set<Service> services
) {
}
