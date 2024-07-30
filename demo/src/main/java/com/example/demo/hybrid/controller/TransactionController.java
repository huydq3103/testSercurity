package com.example.demo.hybrid.controller;

import com.example.demo.hybrid.dto.TransactionDTO;
import com.example.demo.hybrid.service.iplm.TransactionServiceImpl;
import com.example.demo.hybrid.until.constant.CommonConstant;
import com.example.demo.hybrid.until.constant.EncryptionConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

       @GetMapping("/get-transactions")
       public List<TransactionDTO> getTransactions
               (@RequestParam(CommonConstant.PARAM_ENCRYPTED_TRANSACTION_ID) String encryptedTransactionId,
                @RequestParam(EncryptionConstant.PUBLIC_KEY) String publicKey) {

           return transactionService.getTransaction(encryptedTransactionId,publicKey);
       }
}
