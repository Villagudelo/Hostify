package co.edu.uniquindio.application.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Punto de entrada personalizado para manejar excepciones de autenticación en Spring Security.
 * Devuelve una respuesta de error 401 cuando la autenticación falla.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Maneja las excepciones de autenticación enviando una respuesta de error.
     *
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param authException La excepción de autenticación ocurrida.
     * @throws IOException Si ocurre un error al escribir la respuesta.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acceso no autorizado: " + authException.getMessage());
    }



}