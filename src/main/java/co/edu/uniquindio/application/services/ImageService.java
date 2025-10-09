package co.edu.uniquindio.application.services;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ImageService {
    List<String> uploadImages(List<MultipartFile> files) throws Exception;
    String uploadDocument(MultipartFile file) throws Exception;
}
