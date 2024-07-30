package com.example.demo.hybrid.until.encryptions;

import com.example.demo.hybrid.exception.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.hybrid.until.constant.EncryptionConstant;
import com.example.demo.hybrid.until.validate.ValidationUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for generating and saving key pairs.
 */

public class KeyPairGeneratorUtil {

    private static final Logger logger = LoggerFactory.getLogger(KeyPairGeneratorUtil.class);

    /**
     * Generates an RSA key pair and saves the public and private keys to specified file paths.
     *
     * @param publicKeyPath  The file path to save the public key.
     * @param privateKeyPath The file path to save the private key.
     * @throws NoSuchAlgorithmException If the algorithm for generating the key pair is not available.
     * @throws IOException If an I/O error occurs while writing the keys to files.
     */
    public static void generateAndSaveKeyPair(String publicKeyPath, String privateKeyPath) {
        try {
            // Validate file paths
            ValidationUtils.validateFilePath(publicKeyPath);
            ValidationUtils.validateFilePath(privateKeyPath);

            // Tạo đường dẫn cho tệp khóa công khai và khóa riêng
            Paths.get(publicKeyPath).getParent().toFile().mkdirs();
            Paths.get(privateKeyPath).getParent().toFile().mkdirs();

            // Khởi tạo KeyPairGenerator với thuật toán RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(EncryptionConstant.ALGORITHM_RSA);
            keyGen.initialize(2048); // Khóa 2048-bit cho RSA
            KeyPair pair = keyGen.generateKeyPair();

            // Lấy PublicKey và PrivateKey từ KeyPair
            PublicKey publicKey = pair.getPublic();
            PrivateKey privateKey = pair.getPrivate();

            // Lưu khóa công khai dưới dạng chuỗi Base64
            String publicKeyContent = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            Files.write(Paths.get(publicKeyPath), publicKeyContent.getBytes());

            // Lưu khóa riêng dưới dạng chuỗi Base64
            String privateKeyContent = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            Files.write(Paths.get(privateKeyPath), privateKeyContent.getBytes());

            logger.info("RSA Key Pair đã được tạo và lưu trữ thành công.");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Không tìm thấy thuật toán tạo khóa RSA: {}", e.getMessage());
            throw new EncryptionException("Không tìm thấy thuật toán tạo khóa RSA",e);
        } catch (IOException e) {
            logger.error("Lỗi I/O khi lưu khóa RSA: {}", e.getMessage());
            throw new EncryptionException("Lỗi I/O khi lưu khóa RSA",e);
        }
    }

    /**
     * Generates an RSA key pair.
     *
     * @return The generated KeyPair.
     * @throws NoSuchAlgorithmException If the algorithm for generating the key pair is not available.
     */
    public static KeyPair generateKeyRSA() throws NoSuchAlgorithmException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(EncryptionConstant.ALGORITHM_RSA);
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Không tìm thấy thuật toán tạo khóa RSA: {}", e.getMessage());
            throw new EncryptionException("Không tìm thấy thuật toán tạo khóa RSA",e);
        }
    }

    /**
     * Generates an AES key.
     *
     * @return The generated SecretKey.
     * @throws NoSuchAlgorithmException If the algorithm for generating the AES key is not available.
     */
    public SecretKey generateAESKey() throws NoSuchAlgorithmException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(EncryptionConstant.ALGORITHM_AES);
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Không tìm thấy thuật toán tạo khóa AES: {}", e.getMessage());
            throw new EncryptionException("Không tìm thấy thuật toán tạo khóa AES: {}",e);
        }
    }
}
