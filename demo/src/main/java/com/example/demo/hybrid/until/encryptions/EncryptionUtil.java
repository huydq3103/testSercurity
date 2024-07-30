package com.example.demo.hybrid.until.encryptions;

import com.example.demo.hybrid.until.constant.EncryptionConstant;
import com.example.demo.hybrid.until.constant.RegexConstant;
import com.example.demo.hybrid.until.helper.ExceptionHelper;
import com.example.demo.hybrid.until.validate.ValidationUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * A utility class for encryption and decryption operations.
 * <p>
 * This class provides methods for encrypting and decrypting data using AES and RSA algorithms,
 * as well as for loading public and private keys from files or Base64 encoded strings.
 */
@Component
public class EncryptionUtil {

    /**
     * Encrypts data using AES algorithm with the provided AES key.
     *
     * @param data The data to be encrypted.
     * @param key  The AES key used for encryption.
     * @return The encrypted data as a byte array.
     * @throws IllegalArgumentException if the key is null or invalid, or data is null or empty.
     */
    public static byte[] encryptDataWithAES(String data, SecretKey key) {

        ValidationUtils.validateAESKey(key);
        ValidationUtils.validateString(data);

        Cipher cipher = CipherUtils.getCipher(EncryptionConstant.ALGORITHM_AES, Cipher.ENCRYPT_MODE, key);
        return CipherUtils.doFinal(cipher, data.getBytes());

    }

    /**
     * Decrypts data using AES algorithm with the provided AES key.
     *
     * @param encryptedData The encrypted data to be decrypted.
     * @param key           The AES key used for decryption.
     * @return The decrypted data as a byte array.
     * @throws IllegalArgumentException if the key is null or invalid, or encrypted data is null or empty.
     */
    public static byte[] decryptDataWithAES(byte[] encryptedData, SecretKey key) {


        ValidationUtils.validateAESKey(key);
        ValidationUtils.validateByteArray(encryptedData);

        Cipher cipher = CipherUtils.getCipher(EncryptionConstant.ALGORITHM_AES, Cipher.DECRYPT_MODE, key);
        return CipherUtils.doFinal(cipher, encryptedData);


    }

    /**
     * Encrypts an AES key using RSA algorithm with the provided RSA public key.
     *
     * @param aesKey    The AES key to be encrypted.
     * @param publicKey The RSA public key used for encryption.
     * @return The encrypted AES key as a byte array.
     * @throws IllegalArgumentException if the AES key or RSA public key is null or invalid.
     */
    public static byte[] encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) {

        ValidationUtils.validateAESKey(aesKey);
        ValidationUtils.validateRSAKey(publicKey);

        Cipher cipher = CipherUtils.getCipher(EncryptionConstant.ALGORITHM_RSA, Cipher.ENCRYPT_MODE, publicKey);
        return CipherUtils.doFinal(cipher, aesKey.getEncoded());

    }

    /**
     * Decrypts an AES key using RSA algorithm with the provided RSA private key.
     *
     * @param encryptedAESKey The encrypted AES key to be decrypted.
     * @param privateKey      The RSA private key used for decryption.
     * @return The decrypted AES key as a SecretKey object.
     * @throws IllegalArgumentException if the encrypted AES key or RSA private key is null or invalid.
     */
    public static SecretKey decryptAESKeyWithRSA(byte[] encryptedAESKey, PrivateKey privateKey) {


        ValidationUtils.validateByteArray(encryptedAESKey);
        ValidationUtils.validateRSAKey(privateKey);

        Cipher cipher = CipherUtils.getCipher(EncryptionConstant.ALGORITHM_RSA, Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKey = CipherUtils.doFinal(cipher, encryptedAESKey);
        return new SecretKeySpec(decryptedKey, EncryptionConstant.ALGORITHM_AES);


    }

    /**
     * Loads a public key from a file path.
     *
     * @param path The path to the file containing the public key in Base64 encoded format.
     * @return The PublicKey object.
     * @throws IllegalArgumentException if the file path is null or invalid.
     * @throws RuntimeException         if there is an error reading or decoding the key.
     */
    public PublicKey loadPublicKey(String path) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {

        ValidationUtils.validateFilePath(path);

        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        byte[] decodedKey = Base64.getDecoder().decode(new String(keyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance(EncryptionConstant.ALGORITHM_RSA);
        return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));

    }

    /**
     * Loads a private key from a file path.
     *
     * @param path The path to the file containing the private key in Base64 encoded format.
     * @return The PrivateKey object.
     * @throws IllegalArgumentException if the file path is null or invalid.
     * @throws RuntimeException         if there is an error reading or decoding the key.
     */
    public PrivateKey loadPrivateKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        ValidationUtils.validateFilePath(path);

        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        byte[] decodedKey = Base64.getDecoder().decode(new String(keyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance(EncryptionConstant.ALGORITHM_RSA);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));

    }

    /**
     * Loads a public key from a Base64 encoded string.
     *
     * @param key64 The Base64 encoded string representing the public key.
     * @return The PublicKey object.
     * @throws IllegalArgumentException if the Base64 string is null, empty, or invalid.
     * @throws RuntimeException         if there is an error decoding or generating the key.
     */
    public static PublicKey loadPublicKeyFromBase64(String key64) throws InvalidKeySpecException, NoSuchAlgorithmException {

        ValidationUtils.validateBase64String(key64);

        byte[] decodedKey = Base64.getDecoder().decode(key64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(EncryptionConstant.ALGORITHM_RSA);
        return keyFactory.generatePublic(spec);

    }

    /**
     * Loads a private key from a Base64 encoded string.
     *
     * @param key64 The Base64 encoded string representing the private key.
     * @return The PrivateKey object.
     * @throws IllegalArgumentException if the Base64 string is null, empty, or invalid.
     * @throws RuntimeException         if there is an error decoding or generating the key.
     */
    public static PrivateKey loadPrivateKeyFromBase64(String key64) throws InvalidKeySpecException, NoSuchAlgorithmException {

        ValidationUtils.validateBase64String(key64);

        byte[] decodedKey = Base64.getDecoder().decode(key64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(EncryptionConstant.ALGORITHM_RSA);
        return keyFactory.generatePrivate(spec);

    }

    /**
     * Encrypts data using RSA algorithm with the provided RSA public key and returns the encrypted data as a Base64 encoded string.
     *
     * @param data      The data to be encrypted.
     * @param publicKey The RSA public key used for encryption.
     * @return The encrypted data as a Base64 encoded string.
     * @throws IllegalArgumentException if the RSA public key is null or invalid, or data is null or empty.
     */
    public static String encryptDataWithRSA(String data, PublicKey publicKey) {
        ValidationUtils.validateRSAKey(publicKey);
        ValidationUtils.validateString(data);

        Cipher cipher = CipherUtils.getCipher(EncryptionConstant.ALGORITHM_RSA, Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = CipherUtils.doFinal(cipher, data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);

    }

    /**
     * Decrypts data using RSA algorithm with the provided RSA private key and returns the decrypted data as a string.
     *
     * @param encryptedData The Base64 encoded encrypted data.
     * @param privateKey    The RSA private key used for decryption.
     * @return The decrypted data as a string.
     * @throws IllegalArgumentException if the RSA private key is null or invalid, or encrypted data is invalid.
     */
    public static String decryptDataWithRSA(String encryptedData, PrivateKey privateKey) {
        ValidationUtils.validateRSAKey(privateKey);
        ValidationUtils.validateBase64String(encryptedData);

        Cipher cipher = CipherUtils.getCipher(EncryptionConstant.ALGORITHM_RSA, Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = CipherUtils.doFinal(cipher, decodedBytes);
        return new String(decryptedBytes);

    }

    /**
     * Generates a file path for storing a key based on the email or ID and key type.
     *
     * @param emailOrId The email or ID used as part of the file path.
     * @param keyType   The type of key (e.g., "public" or "private").
     * @return The generated file path as a string.
     * @throws IllegalArgumentException if emailOrId or keyType is null or empty.
     */
    public static String generateKeyPath(String emailOrId, String keyType) {
        ValidationUtils.validateString(emailOrId);
        ValidationUtils.validateString(keyType);

        String processedEmailOrId = emailOrId
                .replaceAll(RegexConstant.REGEX_AT, RegexConstant.REPLACE_AT)
                .replaceAll(RegexConstant.REGEX_DOT, RegexConstant.REPLACE_DOT);

        return RegexConstant.KEY_PATH_PREFIX
                + processedEmailOrId
                + RegexConstant.UNDERSCORE + keyType
                + RegexConstant.KEY_PATH_SUFFIX;
    }
}
