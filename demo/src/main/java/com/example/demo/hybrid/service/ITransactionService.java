package com.example.demo.hybrid.service;

import com.example.demo.hybrid.dto.TransactionDTO;
import com.example.demo.hybrid.entity.TransactionHistoryEntity;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {

     void saveTransaction(String encryptedTransactionId, String encryptedSourceAccount,
                                String encryptedDestinationAccount, String amount, String encryptedTime) throws Exception;

     List<TransactionDTO> getTransaction(String encryptedKeyWords, String publicKeyContent);
}
