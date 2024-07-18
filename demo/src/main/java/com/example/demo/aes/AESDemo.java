package com.example.demo.aes;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESDemo {
    public static void main(String[] args) throws Exception {
        // Tạo khóa AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // Kích thước khóa có thể là 128, 192 hoặc 256 bit
        SecretKey secretKey = keyGen.generateKey();

        // Chuyển đổi SecretKey thành dạng có thể lưu trữ và truyền tải
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Key: " + encodedKey);

        // Dữ liệu để mã hóa
        String data = "Hello, World!sss";

        // Mã hóa dữ liệu
        String encryptedData = encrypt(data, secretKey);
        System.out.println("Encrypted Data: " + encryptedData);

        // Giải mã dữ liệu
        String decryptedData = decrypt(encryptedData, secretKey);
        System.out.println("Decrypted Data: " + decryptedData);
    }

    public static String encrypt(String data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
