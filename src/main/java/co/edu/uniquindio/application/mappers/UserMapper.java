package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.user.CreateUserDTO;
import co.edu.uniquindio.application.dto.user.EditUserDTO;
import co.edu.uniquindio.application.dto.user.UserDTO;
import co.edu.uniquindio.application.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // ✅ CORREGIR: Agregar mappings faltantes
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "role", expression = "java(co.edu.uniquindio.application.model.enums.Role.GUEST)")
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.application.model.enums.Status.ACTIVE)")
    @Mapping(target = "description", constant = "")
    @Mapping(target = "legalDocumentUrl", ignore = true)
    @Mapping(target = "password", ignore = true) // Se establece manualmente
    User toEntity(CreateUserDTO userDTO);

    UserDTO toUserDTO(User user);

    // ✅ CORREGIR: Ignorar campos que no deben actualizarse
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateUserFromDto(EditUserDTO dto, @MappingTarget User user);
}