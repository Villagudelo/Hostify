package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.user.*;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.entity.User;
import co.edu.uniquindio.application.model.enums.Status;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public void create(CreateUserDTO userDTO) throws Exception {
        //Validación del email
        if(isEmailDuplicated(userDTO.email())){
            throw new ValueConflictException("El correo electrónico ya está en uso.");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Transformación del DTO a User
        User newUser = userMapper.toEntity(userDTO);

        // Cifrado de la contraseña
        newUser.setPassword(passwordEncoder.encode(userDTO.password()));

        //Almacenamiento del usuario
        userRepository.save(newUser);
    }

    private boolean isEmailDuplicated(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDTO get(String id) throws Exception {
        //Recuperación del usuario
        User user = getUserById(id);

        //Transformación del usuario a DTO
        return userMapper.toUserDTO(user);
    }

    @Override
    public void delete(String id) throws Exception {
        //Recuperación del usuario
        User user = getUserById(id);

        //Eliminación lógica (soft delete)
        user.setStatus(Status.INACTIVE);

        //Se guarda el usuario con el nuevo estado
        userRepository.save(user);
    }

    @Override
    public void edit(String id, EditUserDTO userDTO) throws Exception {
        // Recuperar el usuario desde la base de datos
        User user = getUserById(id);

        // MapStruct se encarga de copiar los valores
        userMapper.updateUserFromDto(userDTO, user);

        // Se guarda el usuario con los nuevos datos
        userRepository.save(user);
    }

    @Override
    public void changePassword(String id, ChangePasswordDTO changePasswordDTO) throws Exception {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Recuperar el usuario desde la base de datos
        User user = getUserById(id);

        // Verificar que la contraseña actual coincida
        if(!passwordEncoder.matches(changePasswordDTO.oldPassword(), user.getPassword())){
            throw new ValidationException("La contraseña actual es incorrecta.");
        }

        // Verificar que la nueva contraseña sea diferente a la actual
        if(changePasswordDTO.oldPassword().equals(changePasswordDTO.newPassword())){
            throw new ValueConflictException("La nueva contraseña no puede ser igual a la actual.");
        }

        // Actualizar la contraseña
        user.setPassword( passwordEncoder.encode(changePasswordDTO.newPassword()) );

        // Guardar el usuario con la nueva contraseña
        userRepository.save(user);

    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) throws Exception {
        // Recuperar el usuario desde la base de datos
        Optional<User> optionalUser = userRepository.findByEmail(resetPasswordDTO.email());

        if(optionalUser.isEmpty()){
            throw new NotFoundException("El usuario no existe");
        }

        //TODO validar que el código que viene en el DTO sea igual al que se envió por email, y que no haya expirado. Luego actualizar la contraseña y eliminar el código usado.

    }


    @Override
    public TokenDTO login(LoginDTO loginDTO) throws Exception {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Recuperar el usuario desde la base de datos
        User user = getUserByEmail(loginDTO.email());

        // Verificar si la contraseña es correcta usando el PasswordEncoder
        if(!passwordEncoder.matches(loginDTO.password(), user.getPassword())){
            throw new NotFoundException("El usuario no existe");
        }

        return new TokenDTO("OK");
    }

    private User getUserById(String id) throws Exception{
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));
    }

    private User getUserByEmail(String email) throws Exception{
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));
    }

}