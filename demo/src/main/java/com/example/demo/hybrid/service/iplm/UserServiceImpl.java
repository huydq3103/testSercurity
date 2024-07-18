package com.example.demo.hybrid.service.iplm;

import com.example.demo.hybrid.entity.UsersEntity;
import com.example.demo.hybrid.exception.EncryptionException;
import com.example.demo.hybrid.repository.UsersRepository;
import com.example.demo.hybrid.service.IUserService;
import com.example.demo.hybrid.until.encryptions.HybridEncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Objects;

import static com.example.demo.hybrid.until.encryptions.KeyPairGeneratorUtil.generateAndSaveKeyPair;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UsersRepository usersRepository;

    private HybridEncryptionUtils encryptionUtil = new HybridEncryptionUtils();

    @Override
    public UsersEntity saveUser(UsersEntity user) throws Exception {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("User must not be null");
        }

        // Define paths for user's keys
        String publicKeyPath = encryptionUtil.generateKeyPath(user.getEmail(), "publicKey");
        String privateKeyPath = encryptionUtil.generateKeyPath(user.getEmail(), "privateKey");

        // Generate and save key pair if not exists
        if (!Files.exists(Paths.get(publicKeyPath)) || !Files.exists(Paths.get(privateKeyPath))) {
            generateAndSaveKeyPair(publicKeyPath, privateKeyPath);
        }

        // Load user's public and private keys
        PublicKey publicKey = encryptionUtil.loadPublicKey(publicKeyPath);
        PrivateKey privateKey = encryptionUtil.loadPrivateKey(privateKeyPath);

        // Generate random AES key
        SecretKey aesKey = encryptionUtil.generateAESKey();

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

    @Override
    public UsersEntity getUser(Long id, String publicKeyContent) throws EncryptionException {
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

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionException("Invalid key specification", e);
        } catch (IOException e) {
            throw new EncryptionException("Error reading key file", e);
        } catch (EncryptionException e) {
            throw new EncryptionException("Failed to decrypt user data", e);
        } catch (Exception e) {
            throw new EncryptionException("Unexpected error while decrypting user data", e);
        }
    }



}
