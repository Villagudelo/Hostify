package co.edu.uniquindio.application.dto.place;

import co.edu.uniquindio.application.model.enums.Service;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public record CreatePlaceDTO(
        @NotBlank @Length(max = 150) String title,
        @NotBlank String description,
        @NotNull int maxGuests,
        @NotNull float nightlyPrice,
        @Size(max = 10) List<String> imageUrls, 
        @Size(max = 10) List<MultipartFile> imageFiles,
        @NotEmpty Set<Service> services,
        @NotNull Double latitude,
        @NotNull Double longitude
) {
}
