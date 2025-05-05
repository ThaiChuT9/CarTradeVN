package com.cartradevn.cartradevn.services.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
     @Value("${upload.path}") // Add this to application.properties
    private String uploadPath;

    public List<String> saveImages(MultipartFile[] images) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + 
                                "_" + 
                                image.getOriginalFilename();
                
                Path path = Paths.get(uploadPath + fileName);
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                
                imageUrls.add(fileName);
            }
        }
        
        return imageUrls;
    }
}
