package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.services.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;

    public ImageServiceImpl() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "TU_CLOUD_NAME",
            "api_key", "TU_API_KEY",
            "api_secret", "TU_API_SECRET"
        ));
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files) throws Exception {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            urls.add(uploadResult.get("secure_url").toString());
        }
        return urls;
    }

    @Override
    public String uploadDocument(MultipartFile file) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), 
            ObjectUtils.asMap("resource_type", "auto", "folder", "legal_documents"));
        return uploadResult.get("secure_url").toString();
    }
}
