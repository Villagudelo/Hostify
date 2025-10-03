package co.edu.uniquindio.application.dto;

public record ResponseDTO<T>(
    boolean error,
    T content
) {
}