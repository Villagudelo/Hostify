package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.user.*;

import java.util.List;

public interface UserService {

    TokenDTO login(LoginDTO loginDTO) throws Exception;

    void create(CreateUserDTO userDTO) throws Exception;

    UserDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    void edit(String id, EditUserDTO userDTO) throws Exception;

    void changePassword(String id, ChangePasswordDTO changePasswordDTO) throws Exception;

    void resetPassword(ResetPasswordDTO resetPasswordDTO) throws Exception;

}
