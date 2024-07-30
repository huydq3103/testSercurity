package com.example.demo.hybrid.service.iplm;

import com.example.demo.hybrid.entity.UsersEntity;
import com.example.demo.hybrid.exception.EncryptionException;
import com.example.demo.hybrid.repository.UsersRepository;
import com.example.demo.hybrid.service.IUserService;
import com.example.demo.hybrid.until.constant.EncryptionConstant;
import com.example.demo.hybrid.until.encryptions.EncryptionUtil;
import com.example.demo.hybrid.until.helper.EncryptionHelper;
import com.example.demo.hybrid.until.validate.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Objects;

/**
 * Implementation of the IUserService interface.
 * Provides functionality for saving and retrieving user data with encryption.
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private UsersRepository usersRepository;

    private EncryptionUtil encryptionUtil = new EncryptionUtil();

    /**
     * Saves a user entity by encrypting sensitive data and storing it along with public and private key paths.
     *
     * @param user The user entity to be saved.
     * @return The saved user entity.
     * @throws Exception If the user is null or any encryption/decryption operation fails.
     */
    @Override
    public UsersEntity saveUser(UsersEntity user) throws Exception {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("User must not be null");
        }

        // Define paths for user's keys
        String publicKeyPath = encryptionUtil.generateKeyPath(user.getEmail(), EncryptionConstant.PUBLIC_KEY);
        String privateKeyPath = encryptionUtil.generateKeyPath(user.getEmail(), EncryptionConstant.PRIVATE_KEY);

        // Generate and save key pair if not exists
        if (!Files.exists(Paths.get(publicKeyPath)) || !Files.exists(Paths.get(privateKeyPath))) {
            EncryptionHelper.generateAndSaveKeyPair(publicKeyPath, privateKeyPath);
        }

        // Load user's public key
        PublicKey publicKey = encryptionUtil.loadPublicKey(publicKeyPath);

        // Generate random AES key
        SecretKey aesKey = EncryptionHelper.generateAESKey();

        // Encrypt sensitive data with AES
        byte[] encryptedPassword = encryptionUtil.encryptDataWithAES(user.getPassword(), aesKey);
        byte[] encryptedEmail = encryptionUtil.encryptDataWithAES(user.getEmail(), aesKey);

        // Encrypt AES key with RSA
        byte[] encryptedAESKey = encryptionUtil.encryptAESKeyWithRSA(aesKey, publicKey);

        // Save encrypted data and key paths
        user.setPassword(Base64.getEncoder().encodeToString(encryptedPassword));
        user.setEmail(Base64.getEncoder().encodeToString(encryptedEmail));
        user.setEncryptedAESKey(Base64.getEncoder().encodeToString(encryptedAESKey));
        user.setPublicKeyPath(publicKeyPath);
        user.setPrivateKeyPath(privateKeyPath);

        return usersRepository.save(user);
    }

    /**
     * Retrieves a user entity by ID and verifies the provided public key.
     * Decrypts the user's sensitive data if the provided public key matches the saved one.
     *
     * @param id The ID of the user to be retrieved.
     * @param publicKeyContent The Base64 encoded public key used for verification.
     * @return The user entity with decrypted sensitive data.
     * @throws EncryptionException If there is a mismatch in public keys or any encryption/decryption operation fails.
     */
    @Override
    public UsersEntity getUser(Long id, String publicKeyContent) throws EncryptionException {

        // Validate inputs
        ValidationUtils.validateString(publicKeyContent);

        if(Objects.isNull(id)){
            throw new IllegalArgumentException("User ID must not be null");
        }

        // Fetch user from repository
        UsersEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        try {
            // Load provided public key from base64 string
            PublicKey providedPublicKey = encryptionUtil.loadPublicKeyFromBase64(publicKeyContent);

            // Load saved public key from file
            PublicKey savedPublicKey = encryptionUtil.loadPublicKey(user.getPublicKeyPath());

            // Verify provided public key matches saved public key
            if (!providedPublicKey.equals(savedPublicKey)) {
                throw new EncryptionException("Provided public key does not match the saved public key for user: " + id);
            }

            // Decrypt AES key with RSA
            byte[] encryptedAESKey = Base64.getDecoder().decode(user.getEncryptedAESKey());
            PrivateKey privateKey = encryptionUtil.loadPrivateKey(user.getPrivateKeyPath());
            SecretKey aesKey = encryptionUtil.decryptAESKeyWithRSA(encryptedAESKey, privateKey);

            // Decrypt sensitive data with AES
            byte[] decryptedPassword = encryptionUtil.decryptDataWithAES(Base64.getDecoder().decode(user.getPassword()), aesKey);
            byte[] decryptedEmail = encryptionUtil.decryptDataWithAES(Base64.getDecoder().decode(user.getEmail()), aesKey);

            // Update entity with decrypted data
            user.setPassword(new String(decryptedPassword, StandardCharsets.UTF_8));
            user.setEmail(new String(decryptedEmail, StandardCharsets.UTF_8));
            return user;

        } catch (EncryptionException e) {
            throw new EncryptionException("Failed to decrypt user data", e);
        } catch (Exception e) {
            throw new EncryptionException("Unexpected error while decrypting user data", e);
        }
    }
}
