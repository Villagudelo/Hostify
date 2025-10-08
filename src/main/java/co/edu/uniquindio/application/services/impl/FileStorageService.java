package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de uploads", ex);
        }
    }

    public String storeFile(MultipartFile file, String userId) {
        try {
            // Validar archivo
            if (file.isEmpty()) {
                throw new ValidationException("El archivo está vacío");
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ValidationException("Solo se permiten archivos de imagen");
            }

            // Generar nombre único
            @SuppressWarnings("null")
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String fileName = userId + "_" + System.currentTimeMillis() + fileExtension;

            // Guardar archivo
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Devolver URL relativa o absoluta
            return "/uploads/" + fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Error al guardar el archivo: " + ex.getMessage(), ex);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.lastIndexOf(".") > 0 ?
                fileName.substring(fileName.lastIndexOf(".")) : ".jpg";
    }
}
