package com.example.demo.hybrid.controller;

import com.example.demo.hybrid.until.constant.RegexConstant;
import com.example.demo.hybrid.until.validate.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/keys")
public class KeyController {

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getKey(@PathVariable String fileName) {
        try {
            // Validate the file name
            ValidationUtils.validateFilePath(fileName);

            // Construct the full path to the file
            Path path = Paths.get(RegexConstant.KEY_PATH_PREFIX + fileName);

            // Check if the file exists
            if (!Files.exists(path)) {
                log.error("File not found: {}", path.toString());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("File not found: " + fileName).getBytes());
            }

            // Read the file content
            byte[] content = Files.readAllBytes(path);

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // Return the response
            return new ResponseEntity<>(content, headers, HttpStatus.OK);

        } catch (IOException e) {
            log.error("Error reading file: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error reading file: " + fileName).getBytes());
        } catch (IllegalArgumentException e) {
            log.error("Invalid file path: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Invalid file path: " + fileName).getBytes());
        }
    }

}
