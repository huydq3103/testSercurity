package com.example.demo.hybrid.until.validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.crypto.SecretKey;
import java.security.Key;

/**
 * A utility class for validating various inputs.
 */
public class ValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    /**
     * Validates that a string is not null or empty.
     *
     * @param str The string to be validated.
     * @throws IllegalArgumentException if the string is null or empty.
     */
    public static void validateString(String str) {
        if (Objects.isNull(str) || str.trim().isEmpty()) {
            logger.error("String validation failed: String cannot be null or empty");
            throw new IllegalArgumentException("String cannot be null or empty");
        }
    }

    /**
     * Validates that a byte array is not null or empty.
     *
     * @param array The byte array to be validated.
     * @throws IllegalArgumentException if the byte array is null or empty.
     */
    public static void validateByteArray(byte[] array) {
        if (Objects.isNull(array) || array.length == 0) {
            logger.error("Byte array validation failed: Byte array cannot be null or empty");
            throw new IllegalArgumentException("Byte array cannot be null or empty");
        }
    }

    /**
     * Validates that an AES key is not null.
     *
     * @param key The AES key to be validated.
     * @throws IllegalArgumentException if the key is null.
     */
    public static void validateAESKey(SecretKey key) {
        if (Objects.isNull(key)) {
            logger.error("AES key validation failed: AES key cannot be null");
            throw new IllegalArgumentException("AES key cannot be null");
        }
    }

    /**
     * Validates that an RSA key is not null.
     *
     * @param key The RSA key to be validated.
     * @throws IllegalArgumentException if the key is null.
     */
    public static void validateRSAKey(Key key) {
        if (Objects.isNull(key)) {
            logger.error("RSA key validation failed: RSA key cannot be null");
            throw new IllegalArgumentException("RSA key cannot be null");
        }
    }

    /**
     * Validates that a file path is not null or empty.
     *
     * @param path The file path to be validated.
     * @throws IllegalArgumentException if the file path is null or empty.
     */
    public static void validateFilePath(String path) {
        if (Objects.isNull(path) || path.trim().isEmpty()) {
            logger.error("File path validation failed: File path cannot be null or empty");
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
    }

    /**
     * Validates that a Base64 string is not null or empty and is a valid Base64 encoded string.
     *
     * @param base64String The Base64 string to be validated.
     * @throws IllegalArgumentException if the Base64 string is null, empty, or invalid.
     */
    public static void validateBase64String(String base64String) {
        if (Objects.isNull(base64String) || base64String.trim().isEmpty()) {
            logger.error("Base64 string validation failed: Base64 string cannot be null or empty");
            throw new IllegalArgumentException("Base64 string cannot be null or empty");
        }
        try {
            Base64.getDecoder().decode(base64String);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 string validation failed", e);
            throw new IllegalArgumentException("Invalid Base64 encoded string", e);
        }
    }


    /**
     * Validates that the provided object is not null and not empty, depending on its type.
     * <p>
     * This method performs the following checks:
     * <ul>
     *     <li>If the object is null, an {@link IllegalArgumentException} is thrown with a message indicating that the object cannot be null.</li>
     *     <li>If the object is a {@link String} and is empty, an {@link IllegalArgumentException} is thrown with a message indicating that the string cannot be empty.</li>
     *     <li>If the object is a {@link Collection} and is empty, an {@link IllegalArgumentException} is thrown with a message indicating that the collection cannot be empty.</li>
     *     <li>If the object is a {@link Map} and is empty, an {@link IllegalArgumentException} is thrown with a message indicating that the map cannot be empty.</li>
     * </ul>
     * </p>
     *
     * @param object the object to be validated
     * @throws IllegalArgumentException if the object is null, or if it is a String, Collection, or Map that is empty
     */
    public static void validateObject(Object object) {
        if (Objects.isNull(object)) {
            logger.error("Object validation failed: Object cannot be null");
            throw new IllegalArgumentException("Object cannot be null");
        }

        if (object instanceof String && ((String) object).isEmpty()) {
            logger.error("Object validation failed: String cannot be empty");
            throw new IllegalArgumentException("String cannot be empty");
        }

        if (object instanceof Collection && ((Collection<?>) object).isEmpty()) {
            logger.error("Object validation failed: Collection cannot be empty");
            throw new IllegalArgumentException("Collection cannot be empty");
        }

        if (object instanceof Map && ((Map<?, ?>) object).isEmpty()) {
            logger.error("Object validation failed: Map cannot be empty");
            throw new IllegalArgumentException("Map cannot be empty");
        }
    }





}
