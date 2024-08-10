package com.wandr.backend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class FileUploadUtil {
    private static final String UPLOAD_DIR = "uploads/";
    //get file, directory and created at timestamp for this method
public static String saveFile(MultipartFile file, String directory) {
    long createdAt = System.currentTimeMillis();
        try {
            if (file.isEmpty()) {
                throw new IOException("Failed to store empty file.");
            }

            // Generate a unique random number
            Random random = new Random();
            int randomNumber = random.nextInt(100000); // Adjust the range if necessary

            // Extract the file extension from the original filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Construct the new filename
            String fileName = randomNumber +  + createdAt + fileExtension;

            // Ensure the uploads directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR + directory);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Resolve the full file path
            Path filePath = uploadPath.resolve(fileName);

            // Save the file to the specified path
            Files.copy(file.getInputStream(), filePath);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    // New deleteFile method
    public static void deleteFile(String directory, String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + directory).resolve(fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new IOException("File not found: " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file " + fileName, e);
        }
    }
}

