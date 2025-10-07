package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.user.CreateUserDTO;
import co.edu.uniquindio.application.dto.user.EditUserDTO;
import co.edu.uniquindio.application.dto.user.UserDTO;
import co.edu.uniquindio.application.model.entity.User;
import co.edu.uniquindio.application.model.enums.Role;
import co.edu.uniquindio.application.model.enums.Status;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-05T21:28:30-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(CreateUserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( userDTO.name() );
        user.phone( userDTO.phone() );
        user.email( userDTO.email() );
        user.password( userDTO.password() );
        user.dateBirth( userDTO.dateBirth() );

        user.id( java.util.UUID.randomUUID().toString() );
        user.status( Status.ACTIVE );
        user.role( Role.GUEST );
        user.createdAt( java.time.LocalDateTime.now() );

        return user.build();
    }

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        String id = null;
        String name = null;
        String email = null;
        String photoUrl = null;
        Role role = null;
        String description = null;
        String legalDocumentUrl = null;

        id = user.getId();
        name = user.getName();
        email = user.getEmail();
        photoUrl = user.getPhotoUrl();
        role = user.getRole();
        description = user.getDescription();
        legalDocumentUrl = user.getLegalDocumentUrl();

        UserDTO userDTO = new UserDTO( id, name, email, photoUrl, role, description, legalDocumentUrl );

        return userDTO;
    }

    @Override
    public void updateUserFromDto(EditUserDTO dto, User user) {
        if ( dto == null ) {
            return;
        }

        user.setName( dto.name() );
        user.setPhone( dto.phone() );
        user.setPhotoUrl( dto.photoUrl() );
        user.setDateBirth( dto.dateBirth() );
        user.setDescription( dto.description() );
        user.setLegalDocumentUrl( dto.legalDocumentUrl() );
    }
}
