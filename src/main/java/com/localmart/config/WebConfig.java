package com.localmart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static String getUploadsDirectory() {
        // Use system temp directory or user home for uploads
        String uploadsPath = System.getProperty("user.home") + File.separator + "localmart_uploads";
        Path uploadDir = Paths.get(uploadsPath);
        try {
            Files.createDirectories(uploadDir);
        } catch (Exception e) {
            // Fallback to project directory
            uploadsPath = "uploads";
            try {
                Files.createDirectories(Paths.get(uploadsPath));
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create uploads directory", ex);
            }
        }
        return uploadsPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadsPath = getUploadsDirectory();
        String uploadLocation = Paths.get(uploadsPath).toAbsolutePath().toUri().toString();
        
        // Ensure path ends with / for proper resource location
        if (!uploadLocation.endsWith("/")) {
            uploadLocation = uploadLocation + "/";
        }
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
    }
}
