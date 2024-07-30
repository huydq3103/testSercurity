package com.example;

import com.example.demo.hybrid.until.encryptions.EncryptionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testInvalidPathException() throws Exception {
        mockMvc.perform(get("/test/invalid-path"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid file path")));
    }

    @Test
    void testIOException() throws Exception {
        mockMvc.perform(get("/test/io-error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error reading file")));
    }

    @Test
    void testIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))

                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error decoding key")));
    }

    @Test
    void testNoSuchAlgorithmException() throws Exception {
        mockMvc.perform(get("/test/no-such-algorithm"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("RSA algorithm not available")));
    }

    @Test
    void testInvalidKeySpecException() throws Exception {
        mockMvc.perform(get("/test/invalid-key-spec"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid key format")));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/invalid-path")
        public void throwInvalidPathException() {
            throw new InvalidPathException("test", "Invalid path");
        }

        @GetMapping("/io-error")
        public void throwIOException() throws IOException {
            throw new IOException("IO Error");
        }

        @GetMapping("/illegal-argument")
        public void throwIllegalArgumentException() {
            throw new IllegalArgumentException("Illegal Argument");
        }

        @GetMapping("/no-such-algorithm")
        public void throwNoSuchAlgorithmException() throws NoSuchAlgorithmException {
            throw new NoSuchAlgorithmException("No Such Algorithm");
        }

        @GetMapping("/invalid-key-spec")
        public void throwInvalidKeySpecException() throws InvalidKeySpecException {
            throw new InvalidKeySpecException("Invalid Key Spec");
        }
    }
}