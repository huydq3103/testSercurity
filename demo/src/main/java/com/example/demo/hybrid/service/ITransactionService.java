package com.example.demo.hybrid.service;

import com.example.demo.hybrid.entity.TransactionHistoryEntity;
import com.example.demo.hybrid.entity.UsersEntity;

import java.math.BigDecimal;

public interface ITransactionService {

    void saveTransaction(String transactionId, String sourceAccount, String destinationAccount) throws Exception;

}
