package com.example;

import com.example.demo.hybrid.service.iplm.TransactionServiceImpl;
import com.example.demo.hybrid.until.constant.EncryptionConstant;
import com.example.demo.hybrid.until.encryptions.EncryptionUtil;
import com.example.demo.hybrid.until.encryptions.KeyPairGeneratorUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.UUID;

@SpringBootApplication
public class SercurityApplication {


    public static void main(String[] args) {
        SpringApplication.run(SercurityApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(TransactionServiceImpl transactionService) {

        return (args) -> {
//            try {
                // Khởi tạo RSA encryption
                EncryptionUtil encryptionUtil = new EncryptionUtil();
                PublicKey publicKey = encryptionUtil.loadPublicKey(EncryptionConstant.PUBLIC_KEY_PATH);
                PrivateKey privateKey = encryptionUtil.loadPrivateKey(EncryptionConstant.PRIVATE_KEY_PATH);

                // Ví dụ giao dịch nợ với dữ liệu hợp lệ
                String transactionIdDebt = UUID.randomUUID().toString();
                String sourceAccountDebt = "AccountA";
                String destinationAccountDebt = "AccountB";
                long amountDebt = 1L; // Số tiền bị trừ

                // Mã hóa các tham số giao dịch bằng RSA
                String encryptedTransactionIdDebt = encryptionUtil.encryptDataWithRSA(transactionIdDebt, publicKey);
                String encryptedSourceAccountDebt = encryptionUtil.encryptDataWithRSA(sourceAccountDebt, publicKey);
                String encryptedDestinationAccountDebt = encryptionUtil.encryptDataWithRSA(destinationAccountDebt, publicKey);
                String encryptedAmount = encryptionUtil.encryptDataWithRSA(Long.toString(amountDebt), publicKey);
                String encryptedTimeDebt = encryptionUtil.encryptDataWithRSA(LocalDate.now().toString(), publicKey);

                // Lưu giao dịch nợ với dữ liệu đã mã hóa
                transactionService.saveTransaction(encryptedTransactionIdDebt, encryptedSourceAccountDebt,
                        encryptedDestinationAccountDebt, encryptedAmount, encryptedTimeDebt);

                System.out.println("Giao dịch đã được lưu thành công!");

                // Test các tình huống lỗi mã hóa
//                System.out.println("Testing encryption errors...");
//
//                // Dữ liệu không thể giải mã (đầu vào không hợp lệ)
//                String invalidEncryptedData = "invalid_data";
//
//                // Thử mã hóa với khóa không hợp lệ
//                try {
//                    encryptionUtil.encryptDataWithRSA(invalidEncryptedData, publicKey);
//                } catch (Exception e) {
//                    System.out.println("Caught exception during encryption with invalid data: " + e.getMessage());
//                }
//
//                // Thử giải mã với khóa không hợp lệ
//                try {
//                    encryptionUtil.decryptDataWithRSA(invalidEncryptedData, privateKey);
//                } catch (Exception e) {
//                    System.out.println("Caught exception during decryption with invalid data: " + e.getMessage());
//                }
//
//                // Thử dữ liệu đầu vào null
//                try {
//                    encryptionUtil.encryptDataWithRSA(null, publicKey);
//                } catch (Exception e) {
//                    System.out.println("Caught exception during encryption with null data: " + e.getMessage());
//                }
//
//                try {
//                    encryptionUtil.decryptDataWithRSA(null, privateKey);
//                } catch (Exception e) {
//                    System.out.println("Caught exception during decryption with null data: " + e.getMessage());
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Có lỗi xảy ra khi thực hiện các thao tác.");
//
        };
    }

}
