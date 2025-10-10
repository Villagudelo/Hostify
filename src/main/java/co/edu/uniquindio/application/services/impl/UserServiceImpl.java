package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.user.*;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.entity.User;
import co.edu.uniquindio.application.model.enums.Status;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.PasswordResetService;
import co.edu.uniquindio.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;

    @Override
    public void create(CreateUserDTO userDTO) throws Exception {
        //Validacion del formato correcto del email
        if (!emailValidation(userDTO.email())) {
            throw new ValidationException("El formato del email no es válido");
        }

        // Validación de contraseña segura
        if (!securePassword(userDTO.password())) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres, una mayúscula y un número");
        }

        //Validación de que no exissta
        if(isEmailDuplicated(userDTO.email())) {
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
        User user = getUserById(id);

        user.setName(userDTO.name());
        user.setPhone(userDTO.phone());
        user.setPhotoUrl(userDTO.photoUrl());
        user.setDateBirth(userDTO.dateBirth());
        user.setDescription(userDTO.description());
        user.setLegalDocumentUrl(userDTO.legalDocumentUrl());

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
        // Validación de nueva contraseña segura
        if (!securePassword(resetPasswordDTO.newPassword())) {
            throw new ValidationException("La nueva contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número");
        }

        passwordResetService.validateCodeAndResetPassword(
                resetPasswordDTO.email(),
                resetPasswordDTO.verificationCode(),
                resetPasswordDTO.newPassword()
        );
    }

    @Override
    public void updatePhoto(String id, String photoUrl) throws Exception {
        User user = getUserById(id);
        user.setPhotoUrl(photoUrl);
        userRepository.save(user);
    }

    @Override
    public UserDTO login(LoginDTO loginDTO) throws Exception {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Recuperar el usuario desde la base de datos
        User user = getUserByEmail(loginDTO.email());

        // Verificar si la contraseña es correcta usando el PasswordEncoder
        if(!passwordEncoder.matches(loginDTO.password(), user.getPassword())){
            throw new NotFoundException("Credenciales inválidas");
        }

        // Retornar el UserDTO con los datos del usuario autenticado
        return userMapper.toUserDTO(user);
    }

    @Override
    public void sendPasswordResetCode(ForgotPasswordDTO forgotPasswordDTO) throws Exception {
        // Verificar que el usuario exista
        getUserByEmail(forgotPasswordDTO.email());
        
        // Delegar al PasswordResetService
        passwordResetService.generateAndSendResetCode(forgotPasswordDTO.email());
    }

    private User getUserById(String id) throws Exception{
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));
    }

    private User getUserByEmail(String email) throws Exception{
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));
    }

    private boolean securePassword(String password){
        // Mínimo 8 caracteres, al menos una mayúscula y un número
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    private boolean emailValidation(String email){
        // Validación básica de formato email
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }
}