package com.example.demo.hybrid.service.iplm;

import com.example.demo.hybrid.entity.TransactionHistoryEntity;
import com.example.demo.hybrid.repository.TransactionHistoryRepository;
import com.example.demo.hybrid.service.ITransactionService;
import com.example.demo.hybrid.until.encryptions.HybridEncryptionUtils;
import com.example.demo.hybrid.until.log.CustomLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.Base64;

import static com.example.demo.hybrid.until.encryptions.KeyPairGeneratorUtil.generateAndSaveKeyPair;

@Service
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    private HybridEncryptionUtils encryptionUtil = new HybridEncryptionUtils();

    /**
     * Saves a transaction with encrypted sensitive data.
     *
     * @param transactionId       The unique identifier for the transaction.
     * @param sourceAccount       The source account number.
     * @param destinationAccount  The destination account number.
     * @throws Exception if encryption or database operation fails.
     */
    @Override
    public void saveTransaction(String transactionId, String sourceAccount, String destinationAccount)
            throws Exception {
        // Check for null parameters
        if (transactionId == null || sourceAccount == null || destinationAccount == null) {
            throw new IllegalArgumentException("Transaction ID, source account, and destination account must not be null");
        }

        // Define paths for user's keys
        String publicKeyPath = encryptionUtil.generateKeyPath(transactionId, "publicKey");
        String privateKeyPath = encryptionUtil.generateKeyPath(transactionId, "privateKey");

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
        byte[] encryptedSourceAccount = encryptionUtil.encryptDataWithAES(sourceAccount, aesKey);
        byte[] encryptedDestinationAccount = encryptionUtil.encryptDataWithAES(destinationAccount, aesKey);

        // Encrypt AES key with RSA
        byte[] encryptedAESKey = encryptionUtil.encryptAESKeyWithRSA(aesKey, publicKey);

        // Log the encrypted transaction details with masked sensitive information
        CustomLogger.logTransaction(transactionId, sourceAccount, destinationAccount);

        // Save to database (two records: one for source account and one for destination account)
        saveTransactionRecord(transactionId, encryptedSourceAccount, encryptedAESKey, publicKeyPath, privateKeyPath);
        saveTransactionRecord(transactionId, encryptedDestinationAccount, encryptedAESKey, publicKeyPath, privateKeyPath);
    }


    /**
     * Saves a single transaction record to the database.
     *
     * @param transactionId     The unique identifier for the transaction.
     * @param encryptedAccount  The encrypted account information.
     * @param encryptedAESKey   The AES key encrypted with RSA.
     * @param publicKeyPath     The path to the public key.
     * @param privateKeyPath    The path to the private key.
     */
    private void saveTransactionRecord(String transactionId, byte[] encryptedAccount
            , byte[] encryptedAESKey, String publicKeyPath, String privateKeyPath) {
        TransactionHistoryEntity transaction = new TransactionHistoryEntity();
        transaction.setTransactionId(transactionId);
        transaction.setAccount(Base64.getEncoder().encodeToString(encryptedAccount));
        transaction.setEncryptedAESKey(Base64.getEncoder().encodeToString(encryptedAESKey));
        transaction.setPublicKeyPath(publicKeyPath);
        transaction.setPrivateKeyPath(privateKeyPath);
        transaction.setTime(LocalDate.now());

        transactionHistoryRepository.save(transaction);
    }



}
