package com.example.demo.hybrid.until.encryptions;

import com.example.demo.hybrid.until.constant.EncryptionConstant;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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

public class HybridEncryptionUtils {

    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(EncryptionConstant.ALGORITHM_AES);
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public byte[] encryptDataWithAES(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(EncryptionConstant.ALGORITHM_AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes());
    }

    public byte[] decryptDataWithAES(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(EncryptionConstant.ALGORITHM_AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    public byte[] encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(EncryptionConstant.ALGORITHM_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKey.getEncoded());
    }

    public SecretKey decryptAESKeyWithRSA(byte[] encryptedAESKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(EncryptionConstant.ALGORITHM_RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKey = cipher.doFinal(encryptedAESKey);
        return new SecretKeySpec(decryptedKey, EncryptionConstant.ALGORITHM_AES);
    }

    public PublicKey loadPublicKey(String path) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        byte[] decodedKey = Base64.getDecoder().decode(new String(keyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance(EncryptionConstant.ALGORITHM_RSA);
        return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
    }

    public PrivateKey loadPrivateKey(String path) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        byte[] decodedKey = Base64.getDecoder().decode(new String(keyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance(EncryptionConstant.ALGORITHM_RSA);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
    }

    public PublicKey loadPublicKeyFromBase64(String key64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(key64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(EncryptionConstant.ALGORITHM_RSA);
        return keyFactory.generatePublic(spec);
    }


    public SecretKey decryptAESKeyWithRSA(byte[] encryptedAESKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(EncryptionConstant.ALGORITHM_RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedKey = cipher.doFinal(encryptedAESKey);
        return new SecretKeySpec(decryptedKey, EncryptionConstant.ALGORITHM_AES);
    }

    public static String encrypt(String data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(EncryptionConstant.ALGORITHM_RSA);
        PublicKey key = keyFactory.generatePublic(spec);

        Cipher cipher = Cipher.getInstance(EncryptionConstant.ALGORITHM_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public String generateKeyPath(String email, String keyType) {
        return "keys/" + email
                .replaceAll("@", "_at_")
                .replaceAll("\\.", "_dot_") + "_" + keyType + ".pem";
    }

}
