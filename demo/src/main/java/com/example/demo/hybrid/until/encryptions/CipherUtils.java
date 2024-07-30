package com.example.demo.hybrid.until.encryptions;

import com.example.demo.hybrid.exception.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for performing encryption and decryption operations using Java's Cipher class.
 */
public class CipherUtils {

    private static final Logger logger = LoggerFactory.getLogger(CipherUtils.class);

    /**
     * Encrypts or decrypts the input data using the provided Cipher instance.
     *
     * @param cipher The Cipher instance configured for encryption or decryption.
     * @param input The input data to be processed.
     * @return The processed data after encryption or decryption.
     * @throws EncryptionException if the cipher operation fails.
     */
    public static byte[] doFinal(Cipher cipher, byte[] input) {
        try {
            return cipher.doFinal(input);
        } catch (Exception e) {
            logger.error("Cipher operation failed: {}", e.getMessage());
            throw new EncryptionException("Cipher operation failed", e);
        }
    }

    /**
     * Initializes a Cipher instance with the specified algorithm, mode, and key.
     *
     * @param algorithm The algorithm to be used by the Cipher.
     * @param mode The operation mode (e.g., Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE).
     * @param key The key to be used for encryption or decryption.
     * @return The initialized Cipher instance.
     * @throws EncryptionException if an error occurs during cipher initialization.
     */
    public static Cipher getCipher(String algorithm, int mode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(mode, key);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Cipher algorithm not found: {}", algorithm);
            throw new EncryptionException("Cipher algorithm not found", e);
        } catch (NoSuchPaddingException e) {
            logger.error("No such padding for algorithm: {}", algorithm);
            throw new EncryptionException("No such padding for algorithm", e);
        } catch (Exception e) {
            logger.error("Error initializing cipher with algorithm: {} and mode: {} - {}", algorithm, mode, e.getMessage());
            throw new EncryptionException("Error initializing cipher", e);
        }
    }

    /**
     * Creates a Cipher instance with the specified algorithm.
     *
     * @param algorithm The algorithm to be used by the Cipher.
     * @return The Cipher instance.
     * @throws EncryptionException if the algorithm is not found or if there is an error during cipher creation.
     */
    public static Cipher getCipher(String algorithm) {
        try {
            return Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Cipher algorithm not found: {}", algorithm);
            throw new EncryptionException("Cipher algorithm not found", e);
        } catch (NoSuchPaddingException e) {
            logger.error("No such padding for algorithm: {}", algorithm);
            throw new EncryptionException("No such padding for algorithm", e);
        }
    }
}
