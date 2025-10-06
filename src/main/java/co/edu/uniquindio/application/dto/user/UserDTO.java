package co.edu.uniquindio.application.dto.user;

import co.edu.uniquindio.application.model.enums.Role;

public record UserDTO(
        String id,
        String name,
        String email,
        String photoUrl,
        Role role,
        String description,
        String legalDocumentUrl
) {
}
