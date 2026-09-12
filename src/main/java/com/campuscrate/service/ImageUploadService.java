package com.campuscrate.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.campuscrate.dto.ImageUploadResponse;
import com.campuscrate.exception.ImageUploadException;

@Service
public class ImageUploadService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private final Cloudinary cloudinary;
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;
    private final String folder;

    public ImageUploadService(Cloudinary cloudinary,
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret,
            @Value("${cloudinary.folder:campus-crate/items}") String folder) {
        this.cloudinary = cloudinary;
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.folder = folder;
    }

    public ImageUploadResponse upload(MultipartFile file) {
        validate(file);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image",
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", false));
            return new ImageUploadResponse(
                    (String) result.get("secure_url"),
                    (String) result.get("public_id"));
        } catch (IOException exception) {
            throw new ImageUploadException("Could not read the uploaded image", exception);
        } catch (RuntimeException exception) {
            throw new ImageUploadException("Image upload to Cloudinary failed", exception);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException("An image file is required");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new ImageUploadException("Only JPEG, PNG, WebP, and GIF images are allowed");
        }
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new ImageUploadException("Cloudinary is not configured on this server");
        }
    }
}
