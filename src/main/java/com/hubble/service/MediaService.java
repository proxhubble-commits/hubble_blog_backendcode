package com.hubble.service;

import com.hubble.dto.*;
import com.hubble.entity.*;
import com.hubble.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaService {
    
    private final MediaAssetRepository mediaRepository;
    private final UserRepository userRepository;
    
    @Value("${media.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${media.base.url:http://localhost:8080}")
    private String baseUrl;

    public MediaUploadResponse uploadImage(Long userId, MultipartFile file) throws IOException {
        return uploadMedia(userId, file, "IMAGE");
    }

    public MediaUploadResponse uploadMedia(Long userId, MultipartFile file, String type) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Invalid file type");
        }
        
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());
        
        MediaAsset media = new MediaAsset();
        media.setUser(user);
        media.setType(type);
        media.setUrl(baseUrl + "/uploads/" + filename);
        media.setFilename(filename);
        media.setOriginalFilename(file.getOriginalFilename());
        media.setMimeType(contentType);
        media.setSizeBytes(file.getSize());
        media.setStorageProvider("local");
        media.setStoragePath(filePath.toString());
        media.setStatus("READY");
        
        media = mediaRepository.save(media);
        return toMediaUploadResponse(media);
    }

    public void deleteMedia(Long userId, Long mediaId) throws IOException {
        MediaAsset media = mediaRepository.findById(mediaId)
            .orElseThrow(() -> new IllegalArgumentException("Media not found"));
        
        if (!media.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        if (media.getUsageCount() > 0) {
            throw new IllegalArgumentException("Media is in use");
        }
        
        if (media.getStorageProvider().equals("local")) {
            Path filePath = Paths.get(media.getStoragePath());
            Files.deleteIfExists(filePath);
        }
        
        mediaRepository.delete(media);
    }

    @Transactional(readOnly = true)
    public Page<MediaListItemDto> getMyMedia(Long userId, String type, Pageable pageable) {
        Page<MediaAsset> media;
        
        if (type != null && !type.isEmpty()) {
            media = mediaRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        } else {
            media = mediaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        return media.map(this::toMediaListItem);
    }

    private MediaUploadResponse toMediaUploadResponse(MediaAsset media) {
        MediaUploadResponse response = new MediaUploadResponse();
        response.setId(media.getId());
        response.setType(media.getType());
        response.setUrl(media.getUrl());
        response.setThumbnailUrl(media.getThumbnailUrl());
        response.setFilename(media.getFilename());
        response.setMimeType(media.getMimeType());
        response.setSizeBytes(media.getSizeBytes());
        response.setWidth(media.getWidth());
        response.setHeight(media.getHeight());
        response.setStatus(media.getStatus());
        response.setCreatedAt(media.getCreatedAt());
        return response;
    }
    
    private MediaListItemDto toMediaListItem(MediaAsset media) {
        MediaListItemDto dto = new MediaListItemDto();
        dto.setId(media.getId());
        dto.setType(media.getType());
        dto.setUrl(media.getUrl());
        dto.setThumbnailUrl(media.getThumbnailUrl());
        dto.setFilename(media.getFilename());
        dto.setSizeBytes(media.getSizeBytes());
        dto.setCreatedAt(media.getCreatedAt());
        return dto;
    }
}
