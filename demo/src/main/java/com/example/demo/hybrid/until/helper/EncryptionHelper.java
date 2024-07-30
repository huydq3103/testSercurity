package com.example.demo.hybrid.until.helper;

import com.example.demo.hybrid.until.encryptions.EncryptionUtil;
import com.example.demo.hybrid.until.encryptions.KeyPairGeneratorUtil;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Helper class for encryption and decryption operations.
 */
public class EncryptionHelper {

    private static final EncryptionUtil encryptionUtil = new EncryptionUtil();
    private static final KeyPairGeneratorUtil keyPairGeneratorUtil = new KeyPairGeneratorUtil();


    /**
     * Encrypts data using AES algorithm.
     *
     * @param data The data to be encrypted.
     * @param aesKey The AES key used for encryption.
     * @return The encrypted data.
     */
    public static byte[] encryptWithAES(String data, SecretKey aesKey) {
        return ExceptionHelper.executeWithExceptionHandling(
                () -> encryptionUtil.encryptDataWithAES(data, aesKey),
                "Error encrypting data with AES"
        );
    }

    /**
     * Generates a new AES key.
     *
     * @return The generated AES key.
     * @throws Exception If there is an error generating the AES key.
     */
    public static SecretKey generateAESKey(){
        return ExceptionHelper.executeWithExceptionHandling(
                () -> keyPairGeneratorUtil.generateAESKey(),
                "Error generating AES key"
        );
    }

    /**
     * Generates an RSA key pair and saves the public and private keys to specified paths.
     *
     * @param publicKeyPath  The path to save the public key.
     * @param privateKeyPath The path to save the private key.
     */
    public static void generateAndSaveKeyPair(String publicKeyPath, String privateKeyPath) {
        ExceptionHelper.executeWithExceptionHandling(
                () -> {
                    keyPairGeneratorUtil.generateAndSaveKeyPair(publicKeyPath, privateKeyPath);
                    return null;
                },
                "Error generating and saving key pair"
        );
    }


    /**
     * Encrypts an AES key using RSA encryption.
     *
     * @param aesKey the AES key to be encrypted
     * @param publicKey the RSA public key used for encryption
     * @return a byte array containing the encrypted AES key
     */
    public static byte[] encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) {
        return ExceptionHelper.executeWithExceptionHandling(
                () -> encryptionUtil.encryptAESKeyWithRSA(aesKey, publicKey),
                "Error encrypting AES key with RSA"
        );
    }

    /**
     * Decrypts an AES key using RSA decryption.
     *
     * @param encryptedAESKey the byte array containing the encrypted AES key
     * @param privateKey the RSA private key used for decryption
     * @return the decrypted AES key
     */
    public SecretKey decryptAESKeyWithRSA(byte[] encryptedAESKey, PrivateKey privateKey) {
        return ExceptionHelper.executeWithExceptionHandling(
                () -> encryptionUtil.decryptAESKeyWithRSA(encryptedAESKey, privateKey),
                "Error decrypting AES key with RSA"
        );
    }



}
