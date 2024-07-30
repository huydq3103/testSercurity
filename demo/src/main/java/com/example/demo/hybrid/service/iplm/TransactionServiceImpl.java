package com.example.demo.hybrid.service.iplm;

import com.example.demo.hybrid.dto.DecryptedTransactionDTO;
import com.example.demo.hybrid.dto.TransactionDTO;
import com.example.demo.hybrid.entity.TransactionHistoryEntity;
import com.example.demo.hybrid.exception.EncryptionException;
import com.example.demo.hybrid.repository.TransactionHistoryRepository;
import com.example.demo.hybrid.service.ITransactionService;
import com.example.demo.hybrid.until.constant.EncryptionConstant;
import com.example.demo.hybrid.until.encryptions.EncryptionUtil;
import com.example.demo.hybrid.until.helper.EncryptionHelper;
import com.example.demo.hybrid.until.helper.ExceptionHelper;
import com.example.demo.hybrid.until.log.CustomLogger;
import com.example.demo.hybrid.until.mapper.TransactionMapper;
import com.example.demo.hybrid.until.validate.ValidationUtils;
import com.mysql.cj.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the ITransactionService interface to handle transaction-related operations.
 */
@Service
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    /**
     * Constructs a TransactionServiceImpl with the public and private keys.
     *
     * @throws Exception If there is an error loading the keys.
     */
    public TransactionServiceImpl() throws Exception {
        this.publicKey = ExceptionHelper.executeWithExceptionHandling(
                () -> new EncryptionUtil().loadPublicKey(EncryptionConstant.PUBLIC_KEY_PATH),
                "Error loading public key from file"
        );

        this.privateKey = ExceptionHelper.executeWithExceptionHandling(
                () -> new EncryptionUtil().loadPrivateKey(EncryptionConstant.PRIVATE_KEY_PATH),
                "Error loading private key from file"
        );
    }

    /**
     * Saves a transaction record after decrypting and encrypting necessary data.
     *
     * @param encryptedTransactionId      The encrypted transaction ID.
     * @param encryptedSourceAccount      The encrypted source account.
     * @param encryptedDestinationAccount The encrypted destination account.
     * @param amount                      The amount involved in the transaction.
     * @param encryptedTime               The encrypted time of the transaction.
     * @throws Exception If there is an error during decryption, encryption, or saving the record.
     */
    @Override
    public void saveTransaction(String encryptedTransactionId, String encryptedSourceAccount,
                                String encryptedDestinationAccount, String amount, String encryptedTime) {

        // Kiểm tra hợp lệ các tham số đầu vào
        ValidationUtils.validateObject(encryptedSourceAccount);
        ValidationUtils.validateObject(encryptedDestinationAccount);
        ValidationUtils.validateString(amount);
        ValidationUtils.validateString(encryptedTime);


        // giải mã dữu liệu
        DecryptedTransactionDTO decryptedData =  decryptTransactionData(encryptedTransactionId, encryptedSourceAccount,
                        encryptedDestinationAccount, amount, encryptedTime, privateKey);

        Long parsedAmount = parseAmount(String.valueOf(decryptedData.getAmount()));

        if (Objects.isNull(parsedAmount)) {
            throw new IllegalArgumentException("Amount must be a valid number");
        }

        // tạo khóa aes
        SecretKey aesKey = EncryptionHelper.generateAESKey();

        // ma hoa key aes key bang rsa
        byte[] encryptedAESKey = EncryptionHelper.encryptAESKeyWithRSA(aesKey, publicKey);

        // mã hóa tài khoản
        byte[] aesEncryptedSourceAccount = EncryptionHelper.encryptWithAES(decryptedData.getSourceAccount(), aesKey);

        byte[] aesEncryptedDestinationAccount =
                EncryptionHelper.encryptWithAES(decryptedData.getDestinationAccount(), aesKey);

        //Tiến hành lưu giao dịch

        saveTransactionRecord(decryptedData.getTransactionId(), aesEncryptedSourceAccount,0L,decryptedData.getAmount(),
                    decryptedData.getTime(), encryptedAESKey);

        saveTransactionRecord(decryptedData.getTransactionId(), aesEncryptedDestinationAccount, decryptedData.getAmount()
                    ,0L, decryptedData.getTime(), encryptedAESKey);


    }

    @Override
    public List<TransactionDTO> getTransaction(String encryptedTransactionId, String publicKeyContent) {

        ValidationUtils.validateString(encryptedTransactionId);
        ValidationUtils.validateString(publicKeyContent);

        // Tìm tất cả các giao dịch với mã giao dịch đã mã hóa
        List<TransactionHistoryEntity> transactions =
                transactionHistoryRepository.findAllByTransactionId(encryptedTransactionId);

        // Dùng stream để xử lý danh sách giao dịch và chuyển đổi thành danh sách DTO
        return transactions.stream()
                .map(transaction -> {
                    // Giải mã khóa AES với RSA
                    byte[] encryptedAESKey = Base64.getDecoder().decode(transaction.getEncryptedAESKey());
                    SecretKey aesKey = EncryptionUtil.decryptAESKeyWithRSA(encryptedAESKey, privateKey);


                    // Giải mã dữ liệu nhạy cảm với AES
                    byte[] decryptedAccount = EncryptionUtil.decryptDataWithAES(
                            Base64.getDecoder().decode(transaction.getAccount()), aesKey);

                    // Cập nhật đối tượng giao dịch đã giải mã
                    transaction.setAccount(new String(decryptedAccount, StandardCharsets.UTF_8));

                    // Chuyển đổi đối tượng TransactionHistoryEntity thành TransactionDTO
                    return TransactionMapper.convertToDTO(transaction);
                })
                .collect(Collectors.toList());
    }


    /**
     * Saves a transaction record to the database.
     *
     * @param transactionId    The transaction ID.
     * @param encryptedAccount The encrypted account information.
     * @param inDebt           The inDebt of the transaction.
     * @param have             The have of the transaction.
     * @param time             The time of the transaction.
     */
    private void saveTransactionRecord(String transactionId, byte[] encryptedAccount,
                                       Long have,Long inDebt, String time, byte[] aesKey) {
        TransactionHistoryEntity transaction = new TransactionHistoryEntity();
        transaction.setTransactionId(transactionId);
        transaction.setAccount(Base64.getEncoder().encodeToString(encryptedAccount));

        transaction.setInDebt(inDebt);
        transaction.setHave(have);

        transaction.setTime(LocalDate.parse(time));
        transaction.setEncryptedAESKey(Base64.getEncoder().encodeToString(aesKey));
        try{
            transactionHistoryRepository.save(transaction);
        }catch (IllegalArgumentException e){
              new IllegalArgumentException("transaction must not null",e);
        }
    }

    /**
     * Decrypts transaction data from encrypted strings using RSA.
     *
     * @param encryptedTransactionId      The encrypted transaction ID.
     * @param encryptedSourceAccount      The encrypted source account.
     * @param encryptedDestinationAccount The encrypted destination account.
     * @param amount                      The amount involved in the transaction.
     * @param encryptedTime               The encrypted time of the transaction.
     * @param privateKey                  The private key used for decryption.
     * @return A DTO containing the decrypted transaction data.
     */
    private DecryptedTransactionDTO decryptTransactionData(String encryptedTransactionId, String encryptedSourceAccount,
                                                           String encryptedDestinationAccount, String amount,
                                                           String encryptedTime, PrivateKey privateKey) {

        String transactionId = EncryptionUtil.decryptDataWithRSA(encryptedTransactionId, privateKey);
        String sourceAccount = EncryptionUtil.decryptDataWithRSA(encryptedSourceAccount, privateKey);
        String destinationAccount = EncryptionUtil.decryptDataWithRSA(encryptedDestinationAccount, privateKey);
        long decryAmount = Long.parseLong(EncryptionUtil.decryptDataWithRSA(amount, privateKey));
        String time = EncryptionUtil.decryptDataWithRSA(encryptedTime, privateKey);

        return new DecryptedTransactionDTO(transactionId, sourceAccount, destinationAccount,
                decryAmount, time);
    }

    /**
     * Parses the amount string into a Long. Returns null if the string is not a valid number.
     *
     * @param amount The amount string to parse.
     * @return The parsed Long value or null if parsing fails.
     */
    private Long parseAmount(String amount) {
        try {
            return Long.valueOf(amount);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
