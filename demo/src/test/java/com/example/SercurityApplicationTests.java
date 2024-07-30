package com.example;

import com.example.demo.hybrid.until.encryptions.EncryptionUtil;
import com.example.demo.hybrid.until.validate.ValidationUtils;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SercurityApplicationTests {
    private static final EncryptionUtil encryptionUtil = new EncryptionUtil();


    @Test
    void testLoadPublicKeyWithInvalidPath() {
        assertThrows(InvalidPathException.class, () -> encryptionUtil.loadPublicKey("invalid/path/to/key"));
    }

    @Test
    void testLoadPublicKeyWithNonExistentFile() {
        assertThrows(IOException.class, () -> encryptionUtil.loadPublicKey("/path/to/nonexistent/file.pub"));
    }

    @Test
    void testLoadPublicKeyWithInvalidContent() throws IOException {
        // Tạo một file tạm với nội dung không hợp lệ
        Path tempFile = Files.createTempFile("invalid", ".pub");
        Files.write(tempFile, "invalid content".getBytes());

        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.loadPublicKey(tempFile.toString()));

        Files.delete(tempFile);
    }


    @Test
    void testLoadPrivateKeyWithInvalidPath() {
        assertThrows(InvalidPathException.class, () -> encryptionUtil.loadPrivateKey("invalid/path/to/key"));
    }

    @Test
    void testLoadPrivateKeyWithNonExistentFile() {
        assertThrows(IOException.class, () -> encryptionUtil.loadPrivateKey("/path/to/nonexistent/file.pem"));
    }



    @Test
    void testGenerateKeyPathNullEmailOrId() {
        assertThrows(IllegalArgumentException.class, () -> EncryptionUtil.generateKeyPath(null, "public"));
    }

    @Test
    void testGenerateKeyPathEmptyEmailOrId() {
        assertThrows(IllegalArgumentException.class, () -> EncryptionUtil.generateKeyPath("", "private"));
    }

    @Test
    void testGenerateKeyPathNullKeyType() {
        assertThrows(IllegalArgumentException.class, () -> EncryptionUtil.generateKeyPath("test@example.com", null));
    }

}
