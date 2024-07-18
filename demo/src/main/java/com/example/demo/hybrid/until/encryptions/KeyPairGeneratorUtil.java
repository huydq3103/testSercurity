package com.example.demo.hybrid.until.encryptions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeyPairGeneratorUtil {

    public static void generateAndSaveKeyPair(String publicKeyPath, String privateKeyPath) throws Exception {
        // Tạo đường dẫn cho tệp khóa công khai và khóa riêng
        Paths.get(publicKeyPath).getParent().toFile().mkdirs();
        Paths.get(privateKeyPath).getParent().toFile().mkdirs();

        // Khởi tạo KeyPairGenerator với thuật toán RSA
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
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

        System.out.println("RSA Key Pair đã được tạo và lưu trữ thành công.");
    }




}
